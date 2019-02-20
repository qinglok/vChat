package me.linx.vchat.app.data.im.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.data.im.session.Attributes
import me.linx.vchat.app.data.repository.MessageRepository
import me.linx.vchat.app.utils.launch
import me.linx.vchat.core.packet.Packet

class AuthResponseHandler : SimpleChannelInboundHandler<Packet.AuthResponsePacket>() {
    override fun messageReceived(ctx: ChannelHandlerContext?, msg: Packet.AuthResponsePacket?) {
        msg?.also { packet ->
            if (packet.isPass) {
                ctx?.pipeline()
                    ?.remove(this)
                    ?.addLast(HeartBeatIdleStateHandler())
                    ?.addLast(HeartBeatHandler())
                    ?.addLast(TextPacketHandler())
                    ?.addLast(LoggedOtherHandler())

                sendUnSend(ctx)
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
