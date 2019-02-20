package me.linx.vchat.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.linx.vchat.core.packet.PacketType;
import me.linx.vchat.core.utils.SecurityUtils;

import java.util.List;

public class Decoder {

    private static Object todo(ByteBuf msg, byte[] aesKey) throws Exception {
        byte[] array;
        final int offset;
        final int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }

        if (aesKey != null) {
            //解密
            ByteBuf buf = Unpooled.wrappedBuffer(array, offset, length);
            byte[] data = new byte[length];
            buf.readBytes(data);
            array = SecurityUtils.AES.decrypt(data, aesKey);
        }

        byte type = array[0];
        return PacketType.getParser(type).parseFrom(array, offset + 1, array.length - 1);
    }

    @ChannelHandler.Sharable
    public static class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            out.add(todo(msg, null));
        }
    }

    @ChannelHandler.Sharable
    public static class ProtobufAESDecoder extends MessageToMessageDecoder<ByteBuf> {
        private final byte[] aesKey;

        public ProtobufAESDecoder(byte[] aesKey) {
            this.aesKey = aesKey;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            out.add(todo(msg, aesKey));
        }
    }
}
