package me.linx.vchat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.linx.vchat.core.codec.Decoder;
import me.linx.vchat.core.codec.Encoder;
import me.linx.vchat.netty.handler.AESRequestHandler;
import me.linx.vchat.netty.handler.ConnectHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
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

    public void start(int port) throws InterruptedException {
        new Timer().schedule(new ShowTask(), 0, 1000);

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
                                    .addLast(new ConnectHandler())
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
