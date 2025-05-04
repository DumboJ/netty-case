package cn.dumboj.netty.chatroom.message.type;

import cn.dumboj.netty.chatroom.message.protocol.AbstractResponseMessage;
import cn.dumboj.netty.chatroom.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseMessage extends AbstractResponseMessage {
    private String from;
    private String content;
    @Override
    public int messageType() {
        return MessageType.CHAT_RESP.getType();
    }
}
