package cn.dumboj.netty.chatroom.message.type;

import cn.dumboj.netty.chatroom.message.protocol.Message;
import cn.dumboj.netty.chatroom.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestMessage extends Message {
    private String username;
    private String password;
    @Override
    public int messageType() {
        return MessageType.LOGIN_REQ.getType();
    }
}
