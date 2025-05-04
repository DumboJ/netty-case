package cn.dumboj.netty.chatroom.boot;

import cn.dumboj.netty.chatroom.codec.ChatByteToMsgDecoder;
import cn.dumboj.netty.chatroom.codec.ChatMsgToByteEncoder;
import cn.dumboj.netty.chatroom.mock.chat.MockClientSendChat;
import cn.dumboj.netty.chatroom.mock.login.MockClientSendLogin;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 客户端
 * */
@Slf4j
public class ClientBoot {
    public static void main(String[] args) {
        ClientBoot clientBoot = new ClientBoot();
        clientBoot.connectServer("localhost",9001);
    }

    private void connectServer(String host,int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler();
        ChatMsgToByteEncoder ENCODER = new ChatMsgToByteEncoder();

        try{
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("lengthDecoder",new LengthFieldBasedFrameDecoder(1024, 7, 4, 0, 0))
                                    .addLast("logging",loggingHandler)
                                    .addLast("chatEncoder",ENCODER)
                                    .addLast("chatDecoder",new ChatByteToMsgDecoder())
                            //回调再其它的 handler 注册绑定后再注册 ChannelInboundHandlerAdapter 回调
//                           .addLast(new MockClientSendChat("Lily","Tide","Hello, this is my Netty Client Msg."))
                           //登录
                           .addLast(new MockClientSendLogin());
                        }
                    });
            Channel channel = client.connect(new InetSocketAddress(host,port)).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            group.shutdownGracefully();
        }
    }
}
