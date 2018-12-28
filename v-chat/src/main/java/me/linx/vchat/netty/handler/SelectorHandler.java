package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;

public class SelectorHandler extends SimpleChannelInboundHandler<Packet.Box> {
    private final TextHandler textHandler = new TextHandler();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.Box box) throws Exception {
        int type =box.getType();
        switch (type) {
            case PacketConstants.TYPE_TEXT:
                textHandler.channelRead(ctx, box.getTextPacket());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
