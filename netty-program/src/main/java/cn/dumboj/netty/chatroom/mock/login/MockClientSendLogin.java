package cn.dumboj.netty.chatroom.mock.login;

import cn.dumboj.netty.chatroom.message.type.LoginRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟 建立连接后 回调channelActive 客户端向服务端发送 login 消息
 * */
@Slf4j
public class MockClientSendLogin extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("mock login channelActive");
        //简历连接后发送 login 登录消息
        LoginRequestMessage admin = new LoginRequestMessage("admin", "123");
        ctx.writeAndFlush(admin);
    }
}
