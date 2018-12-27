import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;

public class AuthHandler extends SimpleChannelInboundHandler<Packet.AuthPacket> {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
//        System.out.println(1);
//    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.AuthPacket msg) throws Exception {
        System.out.println(msg.getToken());
    }


    //    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        PacketBox.AuthPacket packet = (PacketBox.AuthPacket) msg;
//        if (!JwtUtils.check(packet.getToken(), packet.getUserId())){
//            ctx.close();
//        }else {
//            ctx.pipeline().remove(this);
//            super.channelRead(ctx, msg);
//        }
//    }
}
