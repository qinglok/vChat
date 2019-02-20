package me.linx.vchat.app.data.im.handler

import com.blankj.utilcode.util.LogUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import me.linx.vchat.app.constant.AppConfigs
import me.linx.vchat.core.packet.Packet
import java.util.concurrent.TimeUnit

class HeartBeatIdleStateHandler : IdleStateHandler(0, 0, AppConfigs.imIdleTime, TimeUnit.MINUTES) {

    override fun channelIdle(ctx: ChannelHandlerContext?, evt: IdleStateEvent?) {
        ctx?.writeAndFlush(Packet.HeartBeatPacket.newBuilder().build())
        LogUtils.d("Send HeartBeat")
    }
}