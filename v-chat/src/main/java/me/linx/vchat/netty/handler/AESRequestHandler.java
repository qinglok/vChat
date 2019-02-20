package me.linx.vchat.netty.handler;

import com.google.protobuf.ByteString;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.codec.Decoder;
import me.linx.vchat.core.codec.Encoder;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.utils.SecurityUtils;

import javax.crypto.spec.SecretKeySpec;

public class AESRequestHandler extends SimpleChannelInboundHandler<Packet.AESRequestPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.AESRequestPacket box) throws Exception {

        //客户端传递的公钥
        ByteString publicKey = box.getPublicKey();

        //生成AES Key
        SecretKeySpec secretKey = SecurityUtils.AES.newKey();
        byte[] secretKeyBytes = SecurityUtils.AES.getKeyBytes(secretKey);

        //使用公钥把AES Key加密
        byte[] encodedKeybytes = SecurityUtils.RSA.encryptByPublicKey(secretKeyBytes, SecurityUtils.RSA.getPublicKey(publicKey.toByteArray()));

        //构建回送数据
        Packet.AESResponsePacket aesResponsePacket = Packet.AESResponsePacket.newBuilder()
                .setAesKey(ByteString.copyFrom(encodedKeybytes))
                .build();

        //移除自己并添加验证Token处理,等待客户端传递Token
        ctx.pipeline().replace(this, AuthRequestHandler.class.getName(), new AuthRequestHandler());

        //回送给客户端
        ChannelFuture future = ctx.writeAndFlush(aesResponsePacket).sync();
        if (future.isSuccess()){
            // 替换解码器
            ctx.pipeline().replace(Decoder.ProtobufDecoder.class, Decoder.ProtobufAESDecoder.class.getName(), new Decoder.ProtobufAESDecoder(secretKeyBytes));
            // 替换编码器
            ctx.pipeline().replace(Encoder.ProtobufEncoder.class, Encoder.ProtobufAESEncoder.class.getName(), new Encoder.ProtobufAESEncoder(secretKeyBytes));
        }
    }
}
