package cn.dumboj.netty.chatroom.util;
/**
 * 消息类型 枚举
 * */

public enum MessageType {

    HEARTBEAT_REQ(0),
    HEARTBEAT_RESP(1),
    LOGIN_REQ(2),
    LOGIN_RESP(3),
    CHAT_REQ(4),
    CHAT_RESP(5),
    LOGOUT_REQ(6),
    LOGOUT_RESP(7);
    private final int type;
    MessageType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
    public static MessageType getMessageType(int type){
        for (MessageType t : values()) {
            if (t.type == type) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + type);
    }
}
