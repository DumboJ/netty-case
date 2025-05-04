package cn.dumboj.netty.chatroom.message.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息响应消息抽象类
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractResponseMessage extends Message {
    private int code;
    private String reason;
}
