package cn.dumboj.netty.lagou.part1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Netty 实践 ：Http 服务端开发
 * */
public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        startHttpServer(9001);
    }

    /**
     * 启动 Netty Http Server 接收客户端请求并 将请求内容 响应给 客户端
     * 1.创建服务端引导器 2.配置线程模型(Reactor线程模型) 3.绑定业务逻辑处理器并配置网络参数 4.绑定端口启动
     * */
    private static void startHttpServer(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //1. 创建服务端引导器
            ServerBootstrap b = new ServerBootstrap();
            //2.配置线程模型 -Reactor 主从模式
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //设置本地服务端口
                    .localAddress(new InetSocketAddress(port))
                    //3. 绑定业务逻辑处理器
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //Http 编解码
                                    .addLast("codec", new HttpServerCodec())
                                    //HttpContent 压缩
                                    .addLast("compressor", new HttpContentCompressor())
                                    //Http 消息聚合
                                    .addLast("aggregator", new HttpObjectAggregator(65536))
                                    //自定义业务处理器
                                    .addLast("customizerHandler", new HttpServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //4.绑定端口启动  bind() 方法会真正触发启动，sync() 方法则会阻塞直到整个启动过程完成
            ChannelFuture future = b.bind().sync();
            System.out.println("Http Server started. Listening on " + port);
            // 让线程进入 wait 状态，一直处于运行状态 否则执行完会进入 finally 块结束
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    /**
     * 自定义业务逻辑处理器 ： 响应客户端
     * */
    private static class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
            String content = String.format("Http Server received Client Request. uri = %s method = %s content = %s%n",
                    msg.uri(),
                    msg.method(),
                    msg.content().toString(Charset.defaultCharset())
            );
            System.out.println("receive client msg:" + msg.content().toString(CharsetUtil.UTF_8));
            //构造一个 Http 响应返回客户端
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    //ByteBuf 对象
                    Unpooled.wrappedBuffer(content.getBytes())
            );
            //todo why add Listener
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
