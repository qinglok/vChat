package me.linx.vchat.core.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import me.linx.vchat.core.packet.PacketType;
import me.linx.vchat.core.utils.SecurityUtils;

import java.util.List;

public class Encoder {

    private static byte[] createHeader(MessageLite msg) {
        return new byte[]{PacketType.getType(msg)};
    }

    @ChannelHandler.Sharable
    public static class ProtobufEncoder extends MessageToMessageEncoder<MessageLite> {
        @Override
        protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {
            out.add(Unpooled.wrappedBuffer(createHeader(msg), msg.toByteArray()));
        }
    }

    @ChannelHandler.Sharable
    public static class ProtobufAESEncoder extends MessageToMessageEncoder<MessageLite> {
        private final byte[] aesKey;

        public ProtobufAESEncoder(byte[] aesKey) {
            this.aesKey = aesKey;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {
            ByteBuf buf = Unpooled.wrappedBuffer(createHeader(msg), msg.toByteArray());

            byte[] data = new byte[buf.readableBytes()];

            buf.readBytes(data);

            byte[] encryptData = SecurityUtils.AES.encrypt(data, aesKey);
            buf.clear();
            buf.writeBytes(encryptData);

            out.add(buf);
        }
    }
}
