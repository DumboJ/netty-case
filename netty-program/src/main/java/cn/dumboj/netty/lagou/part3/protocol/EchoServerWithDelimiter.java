package cn.dumboj.netty.lagou.part3.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 *  Netty 实现服务端访问 特定分隔符拆包
 * {@link io.netty.handler.codec.DelimiterBasedFrameDecoder}
 * */
public class EchoServerWithDelimiter {
    public static void main(String[] args) throws InterruptedException {
        startDelimiter(9001);
    }

    private static void startDelimiter(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$".getBytes());
                            ch.pipeline()
                                    /**
                                     * 参数说明
                                     * maxFrameLength 最大窗口长度，超过范围没有指定分隔符会抛出异常 配合 failFast 使用，如果 true 超过立即抛出异常，false 会后续字符包含分隔符时抛出
                                     * stripDelimiter 分隔后内容是否包含分隔符
                                     * failFast  io.netty.handler.codec.TooLongFrameException
                                     * delimiter 特定的分隔符
                                     * */
                                    .addLast(new DelimiterBasedFrameDecoder(10, true, true, delimiter))
                                    .addLast(new EchoServerDelimiterHandler());
                        }
                    });
            ChannelFuture future = b.bind().sync();
            System.out.println("server started");
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class EchoServerDelimiterHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf bytebuf = (ByteBuf) msg;
            System.out.println("Receive Client msg:" + bytebuf.toString(CharsetUtil.UTF_8));
        }
    }
}
