package cn.dumboj.netty.sclass.v5.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class JsonObjectClient {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException {
        startJsonClient(9091);
    }

    private static void startJsonClient(int port) throws JsonProcessingException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler());
                        }
                    });
            ChannelFuture future = client.connect(new InetSocketAddress(port));
            System.out.println("client has connected to server,port:" + port);
            Channel channel = future.channel();
            User user = new User(1L, "DaxueshanDalunmingwangTuboGuoshiJiuMozhi");
            ObjectMapper objectMapper = new ObjectMapper();
            String userStr = objectMapper.writeValueAsString(user);

            /** !!! pay attention:
             * java.lang.UnsupportedOperationException:
             * unsupported message type: String (expected: ByteBuf, FileRegion)
             * */
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
            byteBuf.writeCharSequence(userStr, Charset.defaultCharset());
            channel.writeAndFlush(byteBuf).sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
