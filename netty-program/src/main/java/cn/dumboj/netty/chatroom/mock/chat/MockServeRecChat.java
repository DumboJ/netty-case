package cn.dumboj.netty.chatroom.mock.chat;

import cn.dumboj.netty.chatroom.message.type.ChatRequestMessage;
import cn.dumboj.netty.chatroom.message.type.ChatResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * mock 服务端响应客户端 聊天 消息
 * */
@Slf4j
public class MockServeRecChat extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        log.debug("chat msg read client :{}", msg);
        ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(), "success server response chat"));
    }
}
