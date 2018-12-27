package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.NettyServerListener;

public class SelectorHandler extends SimpleChannelInboundHandler<Packet.PacketBox> {
    private final AuthHandler authHandler = new AuthHandler();
    private final TextHandler textHandler = new TextHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyServerListener.clientSum.increment();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        NettyServerListener.clientSum.decrement();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.PacketBox packetBox) throws Exception {
        switch (packetBox.getType()) {
            case AUTH:
                authHandler.channelRead(ctx, packetBox.getAuthPacket());
                break;
            case TEXT:
                textHandler.channelRead(ctx, packetBox.getTextPacket());
                break;
                default:
                    throw new UnsupportedOperationException();
        }
    }
}
