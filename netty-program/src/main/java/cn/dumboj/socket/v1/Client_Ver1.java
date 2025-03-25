package cn.dumboj.socket.v1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Java NIO Socket 编程模型客户端
 * */
public class Client_Ver1 {
    public static void main(String[] args) {
        try {
            SocketChannel client = SocketChannel.open();
            boolean connect = client.connect(new InetSocketAddress(9881));

            System.out.println("this is Netty Socket Client.");
        } catch (IOException e) {
            //ignore
        }
    }
}
