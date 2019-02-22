package me.linx.vchat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import me.linx.vchat.bean.User;
import me.linx.vchat.core.codec.Decoder;
import me.linx.vchat.core.codec.Encoder;
import me.linx.vchat.netty.handler.AESRequestHandler;
import me.linx.vchat.netty.session.IMDispatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;


public class NettyServerListener {

    public static final LongAdder clientSum = new LongAdder();
    public static final LongAdder receiveSum = new LongAdder();
    public static final LongAdder sendSum = new LongAdder();


    static class ShowTask extends TimerTask {
        private long rs = 0;
        private long ss = 0;
        private DateFormat format = SimpleDateFormat.getDateTimeInstance();

        @Override
        public void run() {
            long rsl = receiveSum.longValue();
            long ssl = sendSum.longValue();

            String time = format.format(new Date());

            System.out.println(time + " -> C：" + clientSum.longValue() +
                    " | R：" + rsl +
                    " | S：" + ssl +
                    " | R/s：" + (rsl - rs) +
                    " | S/s：" + (ssl - ss));

            rs = rsl;
            ss = ssl;
        }
    }
    private Object[] getStatusString() {
        List<User> activeUser = IMDispatcher.getActiveUser();
        String[] strings = new String[activeUser.size() + 1];
        strings[0] = "客户端：";

        for (int i = 0; i < activeUser.size(); i++) {
            strings[i+ 1] = activeUser.get(i).getEmail();
        }

        return strings;
    }

    public void start(int port) throws InterruptedException {
//        new Timer().schedule(new ShowTask(), 0, 1000);
        // 启动Gui界面

//        FooGui gui = new FooGui("vChat-Server", this::getStatusString);
//        gui.doShow();

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(16,0,0, TimeUnit.MINUTES){
                                        @Override
                                        protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
                                            super.channelIdle(ctx, evt);
                                            ctx.channel().close();
                                        }
                                    })
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new Decoder.ProtobufDecoder())
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new Encoder.ProtobufEncoder())
                                    .addLast(new AESRequestHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
