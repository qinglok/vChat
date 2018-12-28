package me.linx.vchat.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.linx.vchat.core.constants.PacketConstants;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.utils.JwtUtils;

public class AuthHandler extends SimpleChannelInboundHandler<Packet.Box> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet.Box box) throws Exception {
        if (box.getType() != PacketConstants.TYPE_AUTH){
            return;
        }

        Packet.AuthPacket authPacket = box.getAuthPacket();

        boolean checkOk = JwtUtils.check(authPacket.getToken(), authPacket.getUserId());

        if (checkOk) {
            //验证成功，移除自己并添加业务处理器
            ctx.pipeline().remove(this).addLast(new SelectorHandler());

            System.out.println("Token验证成功！");

            ChannelFuture future = a(ctx, "Token验证成功！");
            if (future.isSuccess()) {

                System.out.println("已回送验证结果，并删除验证处理器");
            }
        } else {
            System.out.println("Token验证失败！");

            ChannelFuture future = a(ctx, "Token验证失败！");
            if (future.isSuccess()) {
                ctx.close();
                System.out.println("已回送验证结果，并关闭连接");
            }
        }
    }

    private ChannelFuture a(ChannelHandlerContext ctx, String msg) throws InterruptedException {
        Packet.TextPacket packet = Packet.TextPacket.newBuilder()
                .setMsg(msg)
                .build();

        Packet.Box box = Packet.Box.newBuilder()
                .setType(PacketConstants.TYPE_TEXT)
                .setTextPacket(packet)
                .build();

        return ctx.writeAndFlush(box).sync();
    }
}
