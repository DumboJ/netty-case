package cn.dumboj.netty.sclass.v5.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * 普通序列化对象的编码发送
 * */
public class ObjectEncoderClient {
    public static void main(String[] args) throws InterruptedException {
        startObjectClient(9091);
    }

    private static void startObjectClient(int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ObjectEncoder())
                                    .addLast(new LoggingHandler());
                        }
                    });
            ChannelFuture future = client.connect(new InetSocketAddress(port)).sync();
            Channel channel = future.channel();
            //java对象序列化 所以User对象必须实现Java序列化接口 Serializable
            User user = new User(2L, "Object client Msg");
            channel.writeAndFlush(user).sync();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
