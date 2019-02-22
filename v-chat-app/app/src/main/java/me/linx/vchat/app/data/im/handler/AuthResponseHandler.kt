package me.linx.vchat.app.data.im.handler

import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.data.im.IMWorker
import me.linx.vchat.app.data.im.session.Attributes
import me.linx.vchat.app.data.repository.MessageRepository
import me.linx.vchat.app.utils.launch
import me.linx.vchat.core.packet.Packet
import java.util.concurrent.TimeUnit

class AuthResponseHandler : SimpleChannelInboundHandler<Packet.AuthResponsePacket>() {
    override fun messageReceived(ctx: ChannelHandlerContext?, msg: Packet.AuthResponsePacket?) {
        msg?.also { packet ->
            if (packet.isPass) {
                ctx?.pipeline()
                    ?.remove(this)
                    ?.addLast(HeartBeatHandler())
                    ?.addLast(TextPacketHandler())
                    ?.addLast(LoggedOtherHandler())

                sendUnSend(ctx)

                PeriodicWorkRequest.Builder(IMWorker::class.java, 8, TimeUnit.MINUTES)
                    .build().also { request ->
                        WorkManager.getInstance().enqueue(request)
                    }
            } else {
                ctx?.channel()?.close()
            }
        }
    }

    private fun sendUnSend(ctx: ChannelHandlerContext?) {
        GlobalScope.launch {
            ctx?.attr(Attributes.user)?.get()?.also { user ->
                val list = MessageRepository.instance.getUnSendAsync(user.bizId ?: 0L).await()
                for (message in list) {
                    Packet.TextPacket.newBuilder()
                        .setFromId(message.fromId ?: 0L)
                        .setToId(message.toId ?: 0L)
                        .setMsg(message.content)
                        .build().also { packet ->
                            ctx.writeAndFlush(packet).addListener { future ->
                                if (future.isSuccess) {
                                    message.sent = true
                                    MessageRepository.instance.saveAsync(message).launch()
                                }
                            }
                        }
                }
            }
        }
    }
}
