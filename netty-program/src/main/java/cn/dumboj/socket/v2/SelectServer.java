package cn.dumboj.socket.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SelectServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9901));
        //关闭阻塞才能注册到 selector 上
        serverSocketChannel.configureBlocking(false);

        //创建监管者并注册到 ServerSocketChannel 上
        Selector selector = Selector.open();

        //两个重要方法 keys()/selectedKeys()
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, null);

        //设置监听的动作
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        while (true) {
            //监听对应事件
            int respNo = selector.select();//此处会有阻塞，需要有特定的事件触发后，才会解除

            //
            System.out.println("start select" + respNo);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sKey = iterator.next();

                //todo 使用之后要将相应的 SelectedKey中元素移除，避免影响后续事件响应操作
                iterator.remove();

                //客户端服务端链接动作
                if (sKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) sKey.channel();
                    SocketChannel socketChannel = server.accept();
                    //io 通信的 channel 设置非阻塞 同样监听状态，write/read
                    socketChannel.configureBlocking(false);
                    //保证 ByteBuffer 容量不足 产生半包或者粘包问题时不会读错数据，将buffer 和对应的事件 channel 绑定
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    SelectionKey sc = socketChannel.register(selector, 0, byteBuffer);
                    sc.interestOps(SelectionKey.OP_READ);

                    //模拟写模式 -- 大量数据 ByteBuffer 容量不能一次写完
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 2000000; i++) {
                        builder.append("s");
                    }
                    ByteBuffer encode = Charset.defaultCharset().encode(builder.toString());
                    int write = socketChannel.write(encode);
                    System.out.println("current write size:" + write);
                    if (encode.hasRemaining()) {
                        //需要继续写,为当前的channel 添加 write 监听
                        sKey.interestOps(sKey.interestOps() + SelectionKey.OP_WRITE);
                        //byteBuff 关联
                        sKey.attach(encode);
                    }
                } else if (sKey.isReadable()) {//读事件
                   //当事件可读时，读取客户端数据
                    SocketChannel socketChannel = (SocketChannel) sKey.channel();


                    ByteBuffer byteBuffer = (ByteBuffer) sKey.attachment();
                    int read = socketChannel.read(byteBuffer);
                    if (read != -1) {
                       /* byteBuffer.flip();
                        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer));*/
                        int limit = byteBuffer.limit();
                        //如果未读完，buffer的position就与limit 相等，则说明ByteBuffer 空间不够用，扩容
                        if (byteBuffer.limit() == byteBuffer.position()) {
                            ByteBuffer newBuff = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                            newBuff.flip();
                            //数据拷贝
                            newBuff.put(byteBuffer);
                            //将新的buff与channel关联
                            sKey.attach(newBuff);
                        }
                    } else if (sKey.isWritable()) {
                        //当无数据读时，客户端关闭会造成服务端一直
                        sKey.cancel();
                    }
                } else if (sKey.isWritable()) {
                    //没写完都会到当前的写事件中
                    SocketChannel channel = (SocketChannel) sKey.channel();
                    ByteBuffer attach = (ByteBuffer) sKey.attachment();

                    //继续写
                    int write = channel.write(attach);
                    System.out.println("continue write size:" + write);

                    if (!attach.hasRemaining()) {//写完后关闭资源
                        attach = null;
                        sKey.interestOps(sKey.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
