package cn.dumboj.netty.chatroom.message.type;

import cn.dumboj.netty.chatroom.message.protocol.Message;
import cn.dumboj.netty.chatroom.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天消息体内容
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestMessage extends Message {
    private String from;
    private String to;
    private String content;
    @Override
    public int messageType() {
        return MessageType.CHAT_REQ.getType();
    }
}
