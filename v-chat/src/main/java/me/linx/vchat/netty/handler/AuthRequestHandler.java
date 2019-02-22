package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.bean.TokenRecord;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.session.Attributes;
import me.linx.vchat.netty.session.IMDispatcher;
import me.linx.vchat.netty.session.TokenRecordSession;
import me.linx.vchat.service.TokenRecordService;

public class AuthRequestHandler extends SimpleChannelInboundHandler<Packet.AuthRequestPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.AuthRequestPacket box) throws Exception {
        TokenRecord tokenRecord = TokenRecordService.instance.verify(box.getToken());
        if (tokenRecord != null) {
            //保存 tokenRecord 到 Session
            TokenRecordSession session = new TokenRecordSession();
            session.setTokenRecord(tokenRecord);
            ctx.channel().attr(Attributes.SESSION).set(session);

            // 绑定用户通道
            IMDispatcher.bind(tokenRecord.getUser().getId(), ctx.channel());

            //验证成功，移除自己并添加业务处理器
            ctx.pipeline()
                    .addLast(new TextPacketHandler())
                    .addLast(new HeartBeatHandler());

            // 回送验证结果 - 通过
            Packet.AuthResponsePacket packet = Packet.AuthResponsePacket.newBuilder()
                    .setIsPass(true)
                    .build();
            ctx.writeAndFlush(packet);

        } else {
            // 回送验证结果 - 未通过
            Packet.AuthResponsePacket packet = Packet.AuthResponsePacket.newBuilder()
                    .setIsPass(false)
                    .build();

            ChannelFuture future = ctx.writeAndFlush(packet).sync();
            if (future.isSuccess()) {
                ctx.close();
            }
        }
    }

}
