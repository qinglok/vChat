package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.netty.NettyServerListener;

public class ConnectHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        NettyServerListener.clientSum.increment();

        //连接到达时，先处理加密
        ctx.pipeline().addLast(new RequestAESKeyHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(Attributes.SESSION).remove();

        NettyServerListener.clientSum.decrement();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(Attributes.SESSION).remove();

        NettyServerListener.clientSum.decrement();

        super.exceptionCaught(ctx, cause);
    }
}
