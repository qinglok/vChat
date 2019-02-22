package me.linx.vchat.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.NettyServerListener;
import me.linx.vchat.netty.session.IMDispatcher;

public class TextPacketHandler extends SimpleChannelInboundHandler<Packet.TextPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.TextPacket packet) throws InterruptedException {
        NettyServerListener.receiveSum.increment();

        long toId = packet.getToId();
        Channel toChannel = IMDispatcher.getChannel(toId);
        if (toChannel != null && toChannel.isOpen()){
            toChannel.writeAndFlush(packet).addListener(future -> {
                if (future.isSuccess()){
                    NettyServerListener.sendSum.increment();
                }
            });
        }
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