package cn.dumboj.netty.lagou.part1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        HttpClient httpClient = new HttpClient();
        httpClient.connect("127.0.0.1", 9001);
    }
    /**
     * 创建 Http 请求客户端
     * */
    private static void connect(String address, int port) throws InterruptedException, URISyntaxException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            //1. 创建引导器
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HttpResponseDecoder())
                                    .addLast(new HttpRequestEncoder())
                                    .addLast(new HttpResponseCustomizerHandler());
                        }
                    });
            ChannelFuture future = b.connect(address,port).sync();
            System.out.println("Http client connected to Server.");
            //mock Http 请求发送到服务端
            DefaultFullHttpRequest request = mockHttpClientRequest(address,port,"netty Http client msg");
            future.channel().writeAndFlush(request);
            // 让线程进入 wait 状态，一直处于运行状态 否则执行完会进入 finally 块结束 todo 实际运行客户端并不阻塞连接状态，存疑
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
    /**
     * 模拟构建浏览器发送的 Http 客户端请求
     *
     * todo 可以直接使用 Netty 提供的 StringEncoder编码器和ChannelOutbounderHandler 直接发送
     * */
    private static DefaultFullHttpRequest mockHttpClientRequest(String address, int port,String content) throws URISyntaxException {
        URI uri = new URI("http://" + address + ":" + port);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                uri.toASCIIString(),
                Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8))
        );
        //设置 Http 请求 Header 键值对
        request.headers().set(HttpHeaderNames.HOST, address)
                .set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return request;
    }

    /**
     * 读取服务端响应自定义逻辑处理器
     * */
    private static class HttpResponseCustomizerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;
                ByteBuf content = httpContent.content();
                System.out.println(content.toString(CharsetUtil.UTF_8));
                content.release();
            }
        }
    }
}
