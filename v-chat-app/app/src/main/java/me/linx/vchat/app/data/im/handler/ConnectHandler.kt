package me.linx.vchat.app.data.im.handler

import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext

class ConnectHandler : ChannelHandlerAdapter() {

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace()
        ctx?.channel()?.close()
    }
}
