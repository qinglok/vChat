package me.linx.vchat.app.data.im

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
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

    companion object {
        private var job: Thread? = null
        private var group: NioEventLoopGroup? = null
        private var channel: Channel? = null

        fun send(message: Message, listener: (Boolean) -> Unit) {
            channel?.let { channel ->
                if (channel.isOpen) {
                    channel.writeAndFlush(
                        Packet.TextPacket.newBuilder()
                            .setFromId(message.fromId ?: 0L)
                            .setToId(message.toId ?: 0L)
                            .setMsg(message.content)
                            .build()
                    )?.addListener { future ->
                        listener(future.isSuccess)
                    }
                }
            }
        }

        fun sendHeartBeat() {
            channel?.let { channel ->
                if (channel.isOpen) {
                    channel.writeAndFlush(Packet.HeartBeatPacket.newBuilder().build())
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "IMService"
            val channelName = "聊天服务"
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val builder = Notification.Builder(this, channelId)
            notification = builder
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(
                    1,
                    notification
                ) //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
            }
        } else {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(this)
                .build()
            startForeground(1, notification)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (job == null || !job!!.isAlive) {
            job = object : Thread() {
                override fun run() {
                    super.run()

                    while (true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(Intent(Utils.getApp(), IMGuardService::class.java))
                        } else {
                            startService(Intent(Utils.getApp(), IMGuardService::class.java))
                        }

                        if (group == null || group!!.isShutdown) {
                            group = NioEventLoopGroup().also { group ->
                                Bootstrap().group(group)
                                    .channel(NioSocketChannel::class.java)
                                    .option(ChannelOption.TCP_NODELAY, true)
                                    .handler(object : ChannelInitializer<SocketChannel>() {
                                        override fun initChannel(ch: SocketChannel?) {
                                            ch?.pipeline()
                                                ?.addLast(ConnectHandler())
                                                ?.addLast(ProtobufVarint32FrameDecoder())
                                                ?.addLast(Decoder.ProtobufDecoder())
                                                ?.addLast(ProtobufVarint32LengthFieldPrepender())
                                                ?.addLast(Encoder.ProtobufEncoder())
                                        }
                                    }).also {
                                        connect(it)
                                    }
                            }
                        }

                        try {
                            sleep(8000L)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            job!!.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun connect(b: Bootstrap) {
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
            // 8秒后重连
            try {
                Thread.sleep(8000L)
            } catch (e: Exception) {
            }
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
                        setupUser()
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
    private fun setupUser() {
        GlobalScope.launch {
            channel?.let { channel ->
                if (channel.isOpen) {
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        LogUtils.file("IMService onDestroy")
        channel?.close()
        group?.shutdownGracefully()
        job?.interrupt()
        super.onDestroy()
    }


}