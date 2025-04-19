package cn.dumboj.netty.lagou.part3.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/***
 * Netty 中对于几种类型的通信协议 解决拆包/粘包 问题的实现
 * {@link FixedLengthFrameDecoder}
 * */
public class EchoServer {
    public static void main(String[] args) throws InterruptedException {
        startServer(9001);
    }
    /**
     * 服务端实现定长拆包 10个字符 一组
     * */
    private static void startServer(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new FixedLengthFrameDecoder(10))
                                    .addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture future = b.bind().sync();
            System.out.println("Echo Server start.");
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * inbound 客户端传输数据打印
     * */
    private static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("Receive client msg :" + byteBuf.toString(StandardCharsets.UTF_8));
        }
    }
}
