package cn.dumboj.netty.sclass.v8.SocketChannelParams;

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
                    // **监听 socket 配置**
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    //设置负责分配每个socketchannel连接的ByteBuf 缓冲区大小，ServerSocketChannel 和 SocketChannel
                    // 都可以设置该属性，但 一般都是使用 childOption设置避免歧义,可以设置单独的接收-发送缓冲区大小
                    .option(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(64 * 1024, 128 * 1024, 256 * 1024))
                    //设置固定缓冲区大小
                    .option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(64 * 1024))
                    // **客户端连接 socket 配置**
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_RCVBUF, 128 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 128 * 1024)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 128 * 1024))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder());
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
