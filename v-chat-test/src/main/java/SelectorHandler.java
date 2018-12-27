import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;

public class SelectorHandler extends SimpleChannelInboundHandler<Packet.PacketBox> {
    private final AuthHandler authHandler = new AuthHandler();
    private final TextHandler textHandler = new TextHandler();

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
