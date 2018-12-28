/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package me.linx.vchat.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.core.session.Session;
import me.linx.vchat.core.utils.AES;

import java.util.List;

@Sharable
public class PacketProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Packet.Box instance = Packet.Box.getDefaultInstance().getDefaultInstanceForType();

    @SuppressWarnings("Duplicates")
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] aesKey = null;

        Session session = ctx.channel().attr(Attributes.SESSION).get();
        if (session != null) {
            aesKey = session.getAesKey();
        }

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
            array = AES.decrypt(data, aesKey);
            out.add(instance.getParserForType().parseFrom(array, offset, array.length));
        }else {
            out.add(instance.getParserForType().parseFrom(array, offset, length));
        }
    }

}
