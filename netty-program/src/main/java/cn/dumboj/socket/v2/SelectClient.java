package cn.dumboj.socket.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SelectClient {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(9901));

            socketChannel.write(StandardCharsets.UTF_8.encode("This is select test client."));
            System.out.println("keep alive");

            //客户端读服务端写
            int read = 0;

            while (true) {
                ByteBuffer allocate = ByteBuffer.allocate(20);
                read += socketChannel.read(allocate);
                //清空继续写
                allocate.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
