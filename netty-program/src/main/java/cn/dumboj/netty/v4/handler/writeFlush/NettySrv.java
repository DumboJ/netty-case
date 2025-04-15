package cn.dumboj.netty.v4.handler.writeFlush;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * Netty 中 pipeline 对于 Handler 的处理流程初识
 * */
public class NettySrv {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //自定义实现解码
                //Netty 会维护 pipeline 中 head -> handler -> handler -> tail
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("Handler1",new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        //！！ 必须设置字符集 不然无法解码打印 ByteBuf 对象
                        //String msgStr = buf.toString();
                        String msgStr = buf.toString(Charset.defaultCharset());
                        System.out.println("Handler 1 msgStr = " + msgStr);
                        //相当于自定义解码 new StringDecoder()
                        super.channelRead(ctx, msgStr);
                    }
                });
                pipeline.addLast("Handler2",new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("Handler 2 msg = " + msg);
                        //向下传递信息
                        super.channelRead(ctx, msg);
                    }
                });
                pipeline.addLast("Handler3",new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("Handler 3 msg = " + msg);
                        //向下传递信息
                        super.channelRead(ctx, msg);
                    }
                });
            }
        });
        serverBootstrap.bind(9001);
    }
}
