import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.linx.vchat.core.packet.Packet;


public class Client {

    Channel channel;

    public Client(String host, int port, EventLoopGroup group) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientInitializer());

        ChannelFuture f = bootstrap.connect(host, port).sync();

        System.out.println("连接" + (f.isSuccess() ? "成功" : "失败"));

        if (f.isSuccess()) {
             channel = ((ChannelFuture) f).channel();
        }
    }



    public void write(Packet.PacketBox box) {
        channel.writeAndFlush(box);
    }

//    public static String toMd5(Packet.TextPacket packet) {
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

