package handler;

import com.google.protobuf.ByteString;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.core.session.Session;
import me.linx.vchat.core.utils.ECC;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class AESResponseHandler extends SimpleChannelInboundHandler<Packet.ResponseAESKeyPacket> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.ResponseAESKeyPacket msg) throws Exception {
        ByteString aesKeyString = msg.getAesKey();
        //服务器回送的加密过的AES Key
        byte[] bytes = aesKeyString.toByteArray();

        Session session = ctx.channel().attr(Attributes.SESSION).get();
        byte[] eccPrivateKey = session.getEccPrivateKey();


        //解密
        byte[] aesKey = ECC.decryptByPrivateKey(bytes, eccPrivateKey);
        //保存到Session，后续的网络数据需要用这个Key加密后才发送
        session.setAesKey(aesKey);

        System.out.println("获取AES成功！");

        System.out.println("开始发送Token验证");

        Packet.AuthPacket packet = Packet.AuthPacket.newBuilder()
                .setUserId(1)
                .setToken("i'm token!")
                .build();

        Packet.Box box = Packet.Box.newBuilder()
                .setType(PacketConstants.TYPE_AUTH)
                .setAuthPacket(packet)
                .build();

        ChannelFuture future = ctx.writeAndFlush(box).sync();
        if (future.isSuccess()){
            System.out.println("发送Token成功！");
        }else {
            System.out.println("发送Token失败！");
        }
    }
}
