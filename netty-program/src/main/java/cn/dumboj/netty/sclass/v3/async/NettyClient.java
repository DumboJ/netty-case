package cn.dumboj.netty.sclass.v3.async;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class NettyClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
           @Override
           protected void initChannel(NioSocketChannel ch) throws Exception {
               ch.pipeline().addLast(new StringEncoder());
           }
        });
        /**
         * 异步阻塞--回调方式实现 {@link io.netty.util.concurrent.Promise}
         * */
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress(9001));
        connect.addListener((ChannelFutureListener) future -> {
            Channel channel = future.channel();
            channel.writeAndFlush("This is Client Listener send msg");
        });
    }
}
