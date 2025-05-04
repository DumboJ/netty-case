package cn.dumboj.netty.chatroom.message.type;

import cn.dumboj.netty.chatroom.message.protocol.Message;
import cn.dumboj.netty.chatroom.util.MessageType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
/**
 * 消息类型和对应的类
 * */
@Data
public class MessageTypeMap {
    public static final Map<Integer, Class<? extends Message>> typeMaps = new HashMap<>();
    static{
        typeMaps.put(MessageType.LOGIN_REQ.getType(), LoginRequestMessage.class);
        typeMaps.put(MessageType.LOGIN_RESP.getType(), LoginResponseMessage.class);
        typeMaps.put(MessageType.CHAT_REQ.getType(), ChatRequestMessage.class);
        typeMaps.put(MessageType.CHAT_RESP.getType(),  ChatResponseMessage.class);
    }
}
