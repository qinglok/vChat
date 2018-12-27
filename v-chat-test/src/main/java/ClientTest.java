import com.google.protobuf.Timestamp;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.linx.vchat.core.packet.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class ClientTest {

    private static LongAdder adder = new LongAdder();

    static final String host = "192.168.0.5";
    static final int port = 8888;

    public static void main(String[] a) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        List<Client> clientList = new ArrayList<>();

        for (int i = 0; i < 2000; i++) {
            if (i % 400 == 0){
                group = new NioEventLoopGroup();
            }
            Client client = new Client(host, port, group);
            clientList.add(client);
            adder.increment();
            System.out.println("连接成功：" + adder.longValue());
        }

        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < clientList.size(); i++) {
            if (i % 100 == 0){
                new TestThread(clients).start();
                clients = new ArrayList<>();
            }
            final Client client = clientList.get(i);
            clients.add(client);
        }
    }

    static class TestThread extends Thread {
        private final List<Client> clientList;
        public TestThread(List<Client> clientList) {
            this.clientList = clientList;
        }

        @Override
        public void run() {
            super.run();
            Packet.TextPacket packet = Packet.TextPacket.newBuilder()
                    .setMsg("hello~~~~~~~~~~~~~")
                    .setCreateTime(Timestamp.getDefaultInstance())
                    .setFromId(123)
                    .setToId(321)
                    .build();
            final Packet.PacketBox box = Packet.PacketBox.newBuilder()
                    .setType(Packet.PacketType.TEXT)
                    .setTextPacket(packet)
                    .build();

            do {
                for (Client client : clientList) {
                    client.write(box);
                }
            } while (true);
        }
    }
}
