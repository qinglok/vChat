package me.linx.vchat;

import me.linx.vchat.netty.NettyServerListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ServletComponentScan // 扫描使用注解方式的servlet
@EnableJpaAuditing
public class VChatApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(VChatApplication.class, args);
    }

    @Override
    public void run(String... args) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    new NettyServerListener().start(port);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

