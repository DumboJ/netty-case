package cn.dumboj.netty.sclass.v6.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * 基础的解析Http请求的实现
 * 普通 入站处理器，需自己识别类型并处理
 * */
@Slf4j
public class BasicHttpServer {
    public static void main(String[] args) throws InterruptedException {
        startBasicServer(9001);
    }

    private static void startBasicServer(int port) throws InterruptedException {
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
                                    .addLast("httpServerCode",new HttpServerCodec())
                                    .addLast("basicHandler",new BasicHttpServerHandler());
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
     * 继承普通入站处理器 自行识别数据类型
     * ChannelInboundHandlerAdapter 普通入站处理器适配器
     * */
    private static class BasicHttpServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                String uri = request.getUri();
                String methodName = request.method().name();
                HttpVersion httpVersion = request.protocolVersion();
                log.info("request uri : {},method:{},HttpVersion:{}", uri, methodName, httpVersion.protocolName());

                //响应客户端
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.OK);
                String responseStr = "This is Server Response.";
                // content
                response.content().writeBytes(responseStr.getBytes());
                //contentLength
                response.headers().set(CONTENT_LENGTH, responseStr.getBytes().length);
                ctx.writeAndFlush(response);
            } else if (msg instanceof HttpContent) {
                //get 请求 HttpContent 为空
                HttpContent httpContent = (HttpContent) msg;
                ByteBuf content = httpContent.content();
                log.info(content.toString());
            }
        }
    }
}
