import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.packet.Packet;

public class TextHandler extends SimpleChannelInboundHandler<Packet.TextPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.TextPacket packet) {
//        System.out.println(packet.getMsg());
//        Packet.TextPacket textPacket = packet.toBuilder()
//                .setMsg("[you]" + packet.getMsg())
//                .build();
//        Packet.PacketBox box = Packet.PacketBox.newBuilder()
//                .setType(Packet.PacketType.TEXT)
//                .setTextPacket(packet)
//                .build();
//        ctx.writeAndFlush(box);
    }


//    public static String toMd5(PacketBox.TextPacket packet) {
//        {
//            try {
//                MessageDigest md5 = MessageDigest.getInstance("MD5");
//                md5.update(packet.toByteArray());
//                byte[] encryption = md5.digest();
//
//                StringBuffer strBuf = new StringBuffer();
//                for (int i = 0; i < encryption.length; i++) {
//                    if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
//                        strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
//                    } else {
//                        strBuf.append(Integer.toHexString(0xff & encryption[i]));
//                    }
//                }
//
//                return strBuf.toString();
//            } catch (NoSuchAlgorithmException e) {
//                return "";
//            }
//        }
//    }

}