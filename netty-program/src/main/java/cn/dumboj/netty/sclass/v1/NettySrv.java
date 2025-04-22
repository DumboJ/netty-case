package cn.dumboj.netty.sclass.v1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Netty 作为服务端实现
 * */
public class NettySrv {
    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        server.channel(NioServerSocketChannel.class);
        server.group(new NioEventLoopGroup());
        server.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                //读取客户端数据解码
                nioSocketChannel.pipeline().addLast(new StringDecoder());
                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("Received Client msg: " + msg);
                    }
                });
            }
        });
        server.bind(9001);
    }
}
