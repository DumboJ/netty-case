package cn.dumboj.netty.chatroom.mock;

import cn.dumboj.netty.chatroom.message.type.ChatRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockChatRequestMessage extends ChannelInboundHandlerAdapter {
    private String from;
    private String to;
    private String content;
    public MockChatRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }
    /**
     * 连接可用后向服务端发送 chat 的 mock 消息
     * */

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("mock chat channelActive");
        ChatRequestMessage chatRequestMessage = new ChatRequestMessage(from, to, content);
        log.info("send msg:{}", chatRequestMessage);
        ctx.writeAndFlush(chatRequestMessage);
    }
}
