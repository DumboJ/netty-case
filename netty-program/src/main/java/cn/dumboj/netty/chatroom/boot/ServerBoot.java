package cn.dumboj.netty.chatroom.boot;

import cn.dumboj.netty.chatroom.codec.ChatByteToMsgDecoder;
import cn.dumboj.netty.chatroom.codec.ChatMsgToByteEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 服务端
 * */
@Slf4j
public class ServerBoot {
    public static void main(String[] args) {
        ServerBoot serverBoot = new ServerBoot();
        serverBoot.start(9001);
    }

    private void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        //可 shareable 的处理器
        LoggingHandler LOG_HANDLER = new LoggingHandler();
        ChatMsgToByteEncoder ENCODER = new ChatMsgToByteEncoder();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //长度域解码器获取数据
                                    .addLast("lengthDecoder",new LengthFieldBasedFrameDecoder(1024,7,4,0,0))
                                    .addLast("logging",LOG_HANDLER)
                                    .addLast("chatEncoder",ENCODER)
                                    .addLast("chatDecoder",new ChatByteToMsgDecoder());
                        }
                    });
            Channel channel = b.bind().sync().channel();
            log.info("服务器启动成功");
            channel.closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
