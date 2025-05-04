package cn.dumboj.netty.chatroom.message.type;

import cn.dumboj.netty.chatroom.message.protocol.AbstractResponseMessage;
import cn.dumboj.netty.chatroom.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginResponseMessage extends AbstractResponseMessage {
    @Override
    public int messageType() {
        return MessageType.LOGIN_RESP.getType();
    }
}
