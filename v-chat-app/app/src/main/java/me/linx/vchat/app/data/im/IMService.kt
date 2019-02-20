package me.linx.vchat.app.data.im

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ServiceUtils
import com.google.protobuf.ByteString
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.entity.Message
import me.linx.vchat.app.data.im.handler.AESResponseHandler
import me.linx.vchat.app.data.im.handler.AuthResponseHandler
import me.linx.vchat.app.data.im.handler.ConnectHandler
import me.linx.vchat.app.data.im.session.Attributes
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.utils.then
import me.linx.vchat.core.codec.Decoder
import me.linx.vchat.core.codec.Encoder
import me.linx.vchat.core.packet.Packet
import me.linx.vchat.core.utils.SecurityUtils

class IMService : Service() {
    private var job: Job? = null
    private var group: NioEventLoopGroup? = null
    private var channel: Channel? = null

    private var notification: Notification? = null

    companion object {
        var instance: IMService? = null
    }

    // AES加密通道建立状态
    private val aesReady by lazy { ObservableField<Boolean>(false) }
    // 电源锁
    private var wakeLock: PowerManager.WakeLock? = null

    init {
        aesReady.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender === aesReady && sender.get()!!) {
                    // 建立AES加密通道完毕后回调
                    setupUser()
                }
            }
        })
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "IMService"
            val channelName = "聊天服务"
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val builder = Notification.Builder(this, channelId)
            notification = builder
//                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        } else {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(applicationContext)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        }

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock =
            pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, javaClass.canonicalName)

        job = GlobalScope.launch {
            while (true) {
                if (!ServiceUtils.isServiceRunning(IMGuardService::class.java)) {
                    ServiceUtils.startService(IMGuardService::class.java)
                }

                if (group == null) {
                    group = NioEventLoopGroup().also { group ->
                        Bootstrap().group(group)
                            .channel(NioSocketChannel::class.java)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(object : ChannelInitializer<SocketChannel>() {
                                override fun initChannel(ch: SocketChannel?) {
                                    ch?.pipeline()
                                        ?.addLast(ProtobufVarint32FrameDecoder())
                                        ?.addLast(Decoder.ProtobufDecoder())
                                        ?.addLast(ProtobufVarint32LengthFieldPrepender())
                                        ?.addLast(Encoder.ProtobufEncoder())
                                        ?.addLast(ConnectHandler())
                                }
                            }).also {

                                connect(it)
                            }
                    }
                }

                delay(8000)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification?.let {
            startForeground(1, it)
        }
        setupUser()
        instance = this
        return Service.START_STICKY
    }

    private suspend fun connect(b: Bootstrap) {
        wakeLock?.acquire()
        try {
            b.connect(Api.imHost, Api.imPort)?.sync()?.also { channelFuture ->
                with(channelFuture) {
                    // 连接成功
                    if (isSuccess) {
                        channel = channel()
                        // 请求 AES 秘钥
                        requestAESKey(channel())
                    } else {
                        channel().close()
                    }
                    // 等待通道关闭（阻塞操作）
                    channel().closeFuture().sync()
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(e)
        } finally {
            aesReady.set(false)

            // 8秒后重连
            delay(8000)
            connect(b)
        }
    }

    /**
     *  请求 AES 秘钥，用于消息加密
     *  AES 秘钥通过 RSA 加密传输
     */
    private fun requestAESKey(channel: Channel) {
        GlobalScope.launch {
            if (channel.isOpen) {
                // 生成 RSA 密钥对
                SecurityUtils.RSA.getKeyPair().also { keyPair ->
                    //保存私钥到Session
                    channel.attr(Attributes.private_key).set(keyPair.private)

                    // 添加 AES 回送处理器
                    channel.pipeline().addLast(AESResponseHandler {
                        aesReady.set(true)
                    })

                    // 构建公钥消息包
                    Packet.AESRequestPacket.newBuilder()
                        .setPublicKey(ByteString.copyFrom(keyPair.public.encoded))
                        .build().also { packet ->
                            // 发送给服务器
                            channel.writeAndFlush(packet)
                        }
                }

            }
        }
    }

    /**
     *  发送校验 Token 请求
     */
    @Synchronized
    private fun setupUser() {
        GlobalScope.launch {
            channel?.let { channel ->
                if (channel.isOpen && aesReady.get()!!) {
                    channel.pipeline()?.addLast(AuthResponseHandler())


                    UserRepository.instance.getByAsync(SPUtils.getInstance().getLong(AppKeys.SP_current_user_id, 0L))
                        .then { user ->
                            channel.attr(Attributes.user).set(user)

                            Packet.AuthRequestPacket.newBuilder()
                                .setToken(user?.token ?: "")
                                .build().also { packet ->
                                    channel.writeAndFlush(packet)
                                }
                        }
                }
            }
        }
    }

    @Synchronized
    fun send(message: Message?, listener :(Boolean) -> Unit) {
        message?.let {
            Packet.TextPacket.newBuilder()
                .setFromId(message.fromId?:0L)
                .setToId(message.toId?:0L)
                .setMsg(message.content)
                .build().also { packet ->
                    channel?.let {channel->
                        if (channel.isOpen){
                            channel.writeAndFlush(packet)?.addListener { future ->
                                listener(future.isSuccess)
                            }
                        }
                    }
                }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        wakeLock?.release()
        aesReady.set(false)
        channel?.close()
        group?.shutdownGracefully()
        job?.cancel()
        stopForeground(true)
    }


}