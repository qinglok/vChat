package me.linx.vchat.core.codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.core.session.Session;
import me.linx.vchat.core.utils.AES;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

@ChannelHandler.Sharable
public class PacketProtobufEncoder extends MessageToMessageEncoder<Packet.Box> {


    @SuppressWarnings("Duplicates")
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet.Box msg, List<Object> out) throws Exception {
        byte[] aesKey = null;

        Session session = ctx.channel().attr(Attributes.SESSION).get();
        if (session != null) {
            aesKey = session.getAesKey();
        }

        if (aesKey == null) {
            out.add(wrappedBuffer(msg.toByteArray()));
        } else {
            ByteBuf buf = wrappedBuffer(msg.toByteArray());
            int size = buf.readableBytes();
            byte[] data = new byte[size];
            buf.readBytes(data);
            byte[] encrypt = AES.encrypt(data, aesKey);
            out.add(wrappedBuffer(encrypt));
        }
    }
}