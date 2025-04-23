package cn.dumboj.netty.sclass.v6.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * 基于 SimpleChannelInboundHandler<I> 解析Http请求的实现,
 * 头体分离 但 SimpleChannelInboundHandler 每次只能处理一种类型
 *          HttpRequest / HttpContent
 * 借助 Netty 封装入站处理器实现 {@link SimpleChannelInboundHandler} ：会在入站 ByteToMessage 的基础上转为 泛型对应的类型
 *              if (acceptInboundMessage(msg)) {
 *                 @SuppressWarnings("unchecked")
 *                 I imsg = (I) msg;
 *                 channelRead0(ctx, imsg);
 *              }
 *
 * */
public class SimpleHttServer {
    public static void main(String[] args) throws InterruptedException {
        startSimpleServer(9001);
    }

    private static void startSimpleServer(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(ServerSocketChannel.class)
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
                                    .addLast(new HttpServerCodec())
                                    //只处理 HttpRequest
                                    .addLast("customizerHttpRequestHandler",new SimpleCustomizerHandler());
                        }
                    });
            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * 基于 SimpleChannelInboundHandler 指定类型的处理器，只处理 HttpRequest
     * */
    private static class SimpleCustomizerHandler extends SimpleChannelInboundHandler<HttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
            String responseContent = String.format("receive client uri:%s ,method:%s,content:%s/n",
                    msg.uri()
                    , msg.method().name()
                    , null);
            //response:version/status/content
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(),
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent.getBytes()));

            response.headers().set(CONTENT_LENGTH, responseContent.getBytes().length);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
