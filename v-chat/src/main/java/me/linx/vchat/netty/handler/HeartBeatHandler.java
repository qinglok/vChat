package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.NettyServerListener;
import me.linx.vchat.netty.session.Attributes;


public class HeartBeatHandler extends SimpleChannelInboundHandler<Packet.HeartBeatPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.HeartBeatPacket heartBeatPacket) throws InterruptedException {
        NettyServerListener.receiveSum.increment();
        System.out.println(
                ctx.attr(Attributes.SESSION).get().getTokenRecord().getUser().getEmail()
                + " HeartBeat");

        ctx.writeAndFlush(Packet.HeartBeatPacket.newBuilder().build()).addListener(future -> {
            if (future.isSuccess()) {
                NettyServerListener.sendSum.increment();
            }
        });
    }
}
