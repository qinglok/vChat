package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;

public class SelectorHandler extends SimpleChannelInboundHandler<Packet.Box> {
    private final AESResponseHandler aesResponseHandler = new AESResponseHandler();
    private final AuthHandler authHandler = new AuthHandler();
    private final TextHandler textHandler = new TextHandler();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.Box box) throws Exception {
        int type =box.getType();
        switch (type) {
            case PacketConstants.TYPE_RESPONSE_AES_KEY:
                aesResponseHandler.channelRead(ctx, box.getResponseAESKeyPacket());
                break;
            case PacketConstants.TYPE_AUTH:
                authHandler.channelRead(ctx,box.getAuthPacket());
                break;
            case PacketConstants.TYPE_TEXT:
                textHandler.channelRead(ctx, box.getTextPacket());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
