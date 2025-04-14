package cn.dumboj.netty.v1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * Netty 作为客户端实现
 * */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap client = new Bootstrap();
        client.channel(NioSocketChannel.class);
        client.group(new NioEventLoopGroup());
        //获取 EventLoop 的方式:通过 NioEventLoopGroup 获取，由 NioEventLoop 由 NioEventLoopGroup 管理
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        EventLoop next = eventExecutors.next();
        client.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new StringEncoder());
            }
        });
        //客户端处理
        ChannelFuture connect = client.connect(new InetSocketAddress(9001));
        connect.sync();
        Channel channel = connect.channel();
        channel.writeAndFlush("This is client msg.");
    }
}
