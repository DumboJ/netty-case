package cn.dumboj.netty.sclass.v7.lifecycle;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
@Slf4j
public class ServerCo {
    public static void main(String[] args) {
        ServerCo server = new ServerCo();
        server.start( 9001);
    }

    private void start( int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            //设置线程模型
            b.group(bossGroup, workerGroup)
                    //设置 channel 类型
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress( port))
                    //设置负责分配每个socketchannel连接的ByteBuf 缓冲区大小，ServerSocketChannel 和 SocketChannel
                    // 都可以设置该属性，但 一般都是使用 childOption设置,还可以设置单独的接收发送缓冲区大小
                    .option(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator())
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    /**
                                     * 自定义关于建立连接的SocketChannel的入站处理器ChannelInboundHandler的生命周期回调
                                     * */
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelRegistered:{}", ctx.channel());
                                            super.channelRegistered(ctx);
                                        }

                                        @Override
                                        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelUnregistered:{}", ctx.channel());
                                            super.channelUnregistered(ctx);
                                        }

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelActive:{}", ctx.channel());
                                            super.channelActive(ctx);
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelInactive:{}", ctx.channel());
                                            super.channelInactive(ctx);
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.info("channelRead:{},msg:{}", ctx.channel(),msg);
                                            super.channelRead(ctx, msg);
                                        }

                                        @Override
                                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelReadComplete:{}", ctx.channel());
                                            super.channelReadComplete(ctx);
                                        }

                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                            log.info("userEventTriggered:{}", ctx.channel());
                                            super.userEventTriggered(ctx, evt);
                                        }

                                        @Override
                                        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                                            log.info("channelWritabilityChanged:{}", ctx.channel());
                                            super.channelWritabilityChanged(ctx);
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            log.info("exceptionCaught:{}", ctx.channel());
                                            super.exceptionCaught(ctx, cause);
                                        }
                                    })
                                    .addLast(new ChannelInboundHandlerAdapter(){
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            super.channelRead(ctx, msg);
                                        }
                                    });
                        }
                    });
            //绑定，监听 chanel 关闭
            ChannelFuture future = b.bind().sync();
            Channel channel = future.channel();

            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
