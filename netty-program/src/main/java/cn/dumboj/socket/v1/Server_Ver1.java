package cn.dumboj.socket.v1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Java NIO Socket 编程模型服务端
 * */
public class Server_Ver1 {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocketChannel server = serverSocketChannel.bind(new InetSocketAddress(9881));

            //服务端不再阻塞accept()链接
            server.configureBlocking(false);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            List<SocketChannel> channelList = new ArrayList<>();

            while (true) {

                SocketChannel socketChannel = server.accept();//blocked client connect，此处阻塞，socket 连接

                //如果客户端链接不为空，则添加
                if (socketChannel != null) {
                    //设置 SocketChannel IO通信不阻塞
                    socketChannel.configureBlocking(false);
                    //模拟链接多个客户端
                    channelList.add(socketChannel);
                }

                for (SocketChannel temp : channelList) {

                    int read = temp.read(byteBuffer);//blocked 此处阻塞，io数据通信
                    if (read > 0) {
                        System.out.println("通信开始 ... ...");
                        byteBuffer.flip();
                        CharBuffer decodeBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                        System.out.println(decodeBuffer);

                        //调整写模式，供下次使用
                        byteBuffer.clear();

                        System.out.println("通信结束 ... ...");
                    }

                }


            }

        } catch (IOException e) {
            //ignore
        }
    }
}
