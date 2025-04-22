package cn.dumboj.netty.sclass.v2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 分工的 Netty 服务端
 * */
public class NettySrv {
    public static void main(String[] args) {
        //主 accept 1
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //从 work IO操作 多个
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        //辅助的 EventLoop
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringDecoder());
                //pipeline 的重载方法
                //ChannelPipeline addLast(EventExecutorGroup group, ChannelHandler... handlers);
                ch.pipeline().addLast(defaultEventLoop, new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("msg = " + msg);
                    }
                });
            }
        });
        serverBootstrap.bind(9001);
    }
}
