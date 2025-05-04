package cn.dumboj.netty.chatroom.mock.login;

import cn.dumboj.netty.chatroom.message.type.LoginRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * mock 服务端接收到登录消息 并向客户端响应接收成功消息
 */
@Slf4j
public class MockServerRecLogin extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        log.debug("login msg read client login :{}", msg);
        ctx.writeAndFlush(new LoginRequestMessage("200", "success server response login"));
    }
}
