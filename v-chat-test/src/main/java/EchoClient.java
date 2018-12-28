/*
 * Copyright 2012 The Netty Project
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

import com.google.protobuf.ByteString;
import handler.SelectorHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import me.linx.vchat.core.codec.PacketProtobufDecoder;
import me.linx.vchat.core.codec.PacketProtobufEncoder;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.core.session.Attributes;
import me.linx.vchat.core.session.Session;
import me.linx.vchat.core.utils.ECC;

import java.security.KeyPair;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class EchoClient {

    public static void main(String[] args) throws Exception {

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                             .addLast(new ProtobufVarint32FrameDecoder())
                             .addLast(new PacketProtobufDecoder())
                             .addLast(new ProtobufVarint32LengthFieldPrepender())
                             .addLast(new PacketProtobufEncoder())
                             .addLast(new SelectorHandler());
                 }
             });

            // Start the client.
            ChannelFuture f = b.connect("192.168.0.5", 8888).sync();

            if (f.isSuccess()){

                KeyPair keyPair = ECC.getKeyPair();
                byte[] encoded = keyPair.getPublic().getEncoded();

                //保存私钥
                Session session = new Session();
                session.setEccPrivateKey(keyPair.getPrivate().getEncoded());
                f.channel().attr(Attributes.SESSION).set(session);

                Packet.RequestAESKeyPacket packet = Packet.RequestAESKeyPacket.newBuilder()
                        .setPublicKey(ByteString.copyFrom(encoded))
                        .build();
                Packet.Box box = Packet.Box.newBuilder()
                        .setType(PacketConstants.TYPE_REQUEST_AES_KEY)
                        .setRequestAESKeyPacket(packet)
                        .build();

                ByteBuf buf = Unpooled.copyInt(PacketConstants.TYPE_REQUEST_AES_KEY);
                buf.writeBytes(encoded);

                System.out.println("输如任意键发送AES请求...");
                System.in.read();

                ChannelFuture channelFuture = f.channel().writeAndFlush(box).sync();
                if(channelFuture.isSuccess()){
                    System.out.println("发送AES请求成功！");
                }else {
                    System.out.println("发送AES请求失败！");
                }
            }

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
