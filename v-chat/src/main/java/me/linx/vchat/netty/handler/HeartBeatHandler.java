package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.NettyServerListener;


public class HeartBeatHandler extends SimpleChannelInboundHandler<Packet.HeartBeatPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Packet.HeartBeatPacket heartBeatPacket) {
        NettyServerListener.receiveSum.increment();

        channelHandlerContext.writeAndFlush(Packet.HeartBeatPacket.newBuilder().build());

        NettyServerListener.sendSum.increment();
    }
}
