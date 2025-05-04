package cn.dumboj.netty.chatroom.handler;

import cn.dumboj.netty.chatroom.message.type.ChatRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String to = msg.getTo();
        String content = msg.getContent();
        log.info("receive from:{},to:{},content:{}", from, to, content);
    }
}
