package cn.dumboj.netty.sclass.v7.lifecycle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class ClientCo {
    public static void main(String[] args) {
        ClientCo clientCo = new ClientCo();
        clientCo.start(9001);
    }

    private void start(int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(port))
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        /* 这里 由 bootstrap 主线程调用，如果写，可能产生线程安全问题，Netty要求所有IO操作都由EventLoop完成*/
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new LoggingHandler());
                            /**
                             * 此处 Channel 刚创建，可能还没激活连接到服务器，并且没有绑定到 EventLoop
                             * */
                            //ch.writeAndFlush("ctx write and flush");
                        }
                    });
            ChannelFuture future = client.connect().sync();
            Channel channel = future.channel();
            channel.writeAndFlush("this is client msg.");
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
