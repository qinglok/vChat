package me.linx.vchat.netty.handler;

import com.google.protobuf.ByteString;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.core.session.Session;
import me.linx.vchat.core.utils.AES;
import me.linx.vchat.core.utils.ECC;

import javax.crypto.spec.SecretKeySpec;

public class RequestAESKeyHandler extends SimpleChannelInboundHandler<Packet.Box> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.Box box) throws Exception {
        if (box.getType() != PacketConstants.TYPE_REQUEST_AES_KEY) {
            return;
        }

        Packet.RequestAESKeyPacket msg = box.getRequestAESKeyPacket();

        //客户端传递的公钥
        ByteString publicKey = msg.getPublicKey();

        //生成AES Key
        SecretKeySpec secretKey = AES.newSecretKey();
        byte[] secretKeyBytes = AES.getKeyBytes(secretKey);

        //使用公钥把AES Key加密
        byte[] encodedKeybytes = ECC.encryptByPublicKey(secretKeyBytes, ECC.getPublicKey(publicKey.toByteArray()));

        //构建回送数据
        Packet.ResponseAESKeyPacket packet = Packet.ResponseAESKeyPacket.newBuilder()
                .setAesKey(ByteString.copyFrom(encodedKeybytes))
                .build();

        //构建外层Box
        Packet.Box rBox = Packet.Box.newBuilder()
                .setType(PacketConstants.TYPE_RESPONSE_AES_KEY)
                .setResponseAESKeyPacket(packet)
                .build();

        //移除自己并添加验证Token处理,等待客户端传递Token
        ctx.pipeline().remove(this).addLast(new AuthHandler());

        //回送给客户端
        ChannelFuture future = ctx.writeAndFlush(rBox).sync();
        if (future.isSuccess()) {
            //保存AES Key到Session
            Session session = new Session();
            session.setAesKey(secretKeyBytes);
            ctx.channel().attr(Attributes.SESSION).set(session);
        }
    }
}
