package me.linx.vchat.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import me.linx.vchat.bean.User;
import me.linx.vchat.core.packet.Packet;
import me.linx.vchat.netty.NettyServerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IMDispatcher {
    private IMDispatcher() {
    }

    private static final ConcurrentHashMap<Long, Channel> channelMap = new ConcurrentHashMap<>();

    public synchronized static void bind(Long userId, Channel channel) {
        unBind(userId);
        channelMap.put(userId, channel);
        NettyServerListener.clientSum.increment();

        channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
            channelMap.remove(userId);
            NettyServerListener.clientSum.decrement();
        });
    }

    public static void unBind(Long userId) {
        Channel old = channelMap.get(userId);
        if (old != null) {
            old.close();
        }
    }

    /**
     * 发送异地登录通知，并移除通道
     *
     * @param userId {@link me.linx.vchat.bean.User} ID
     */
    public static void unBindWithLoggedOther(Long userId) {
        Channel old = channelMap.get(userId);
        if (old != null) {
            try {
                if (old.isOpen()){
                    Packet.LoggedOtherPacket packet = Packet.LoggedOtherPacket.newBuilder().build();
                    old.writeAndFlush(packet).sync();
                }
                old.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Channel getChannel(Long userId) {
        return channelMap.get(userId);
    }

    public static List<User> getActiveUser(){
        List<User> list = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            TokenRecordSession recordSession = channel.attr(Attributes.SESSION).get();
            list.add(recordSession.getTokenRecord().getUser());
        }
        return list;
    }
}
