package cn.dumboj.netty.sclass.v6.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * 基于 SimpleChannelInboundHandler<DefaultFullHttpRequest> 解析Http请求的实现
 * {@link FullHttpRequest} 的实现基础是前置处理对 HttpRequest 和 HttpContent 做聚合操作
 * {}
 * */
public class FullHttServer {
    public static void main(String[] args) throws InterruptedException {
        startSimpleServer(9001);
    }

    private static void startSimpleServer(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler())
                                    /**
                                     * extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder>
                                     *     HttpCodec 是复合的编解码器
                                     *     接收时 HttpRequestDecoder 解码
                                     *     响应时 HttpResponseEncoder 编码
                                     *     */
                                    .addLast("serverCodec",new HttpServerCodec())
                                    /**
                                     * Http 聚合器，聚合 Http 头体内容
                                     *
                                     * extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage>
                                     *
                                     * 本质是 MessageToMessage 类型的解码器，同时聚合 Http 请求中 HttpObject ,HttpMessage.等信息
                                     * */
                                    .addLast("aggregator",new HttpObjectAggregator(65536))
                                    .addLast("customizerHandler",new CustomizerHandler());
                        }
                    });
            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class CustomizerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            String responseContent = String.format("receive client uri:%s ,method:%s,content:%s/n",
                    msg.uri()
                    , msg.method().name()
                    , msg.content());
            System.out.println(responseContent);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(),
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent.getBytes()));
            response.headers().set(CONTENT_LENGTH, responseContent.getBytes().length);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
