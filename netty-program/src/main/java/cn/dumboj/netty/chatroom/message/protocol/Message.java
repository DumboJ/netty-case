package cn.dumboj.netty.chatroom.message.protocol;

import lombok.Data;

/**
 * 消息协议抽象类
 * */
@Data
public abstract class Message {
   /*public static final int MAGIC_NUMBER = 0xAA;
   public static final int VERSION = 1;
   private static final int TYPE_LENGTH = 4;
   private static final int DATA_LENGTH = 4;*/
   public abstract int messageType();
}
