package me.linx.vchat.app.data.im.handler

import com.blankj.utilcode.util.LogUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import me.linx.vchat.core.packet.Packet

class HeartBeatHandler : SimpleChannelInboundHandler<Packet.HeartBeatPacket>() {

    override fun messageReceived(ctx: ChannelHandlerContext?, p1: Packet.HeartBeatPacket?) {
        LogUtils.d("Received HeartBeat")
    }


}
