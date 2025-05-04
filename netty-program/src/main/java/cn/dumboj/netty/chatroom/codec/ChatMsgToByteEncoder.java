package cn.dumboj.netty.chatroom.codec;

import cn.dumboj.netty.chatroom.message.protocol.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
/**
 * 自定义协议编码器 长度偏移4字节
 * */
@ChannelHandler.Sharable
public class ChatMsgToByteEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf bf) throws Exception {
        //协议魔数 4字节
        bf.writeBytes(new byte[]{'y', 'y', 'm', 'x'});

        //协议版本 1 字节
        bf.writeByte(1);

        //序列化算法 1.json 2.protobuf 1字节
        bf.writeByte(1);

        //消息类型 1字节  int -> byte
        bf.writeByte(msg.messageType());

        //消息长度 4 字节
        String content = new ObjectMapper().writeValueAsString(msg);
        bf.writeInt(content.length());

        //消息内容
        bf.writeCharSequence(content, StandardCharsets.UTF_8);
    }
}
