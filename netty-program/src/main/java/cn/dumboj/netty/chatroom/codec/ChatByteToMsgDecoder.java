package cn.dumboj.netty.chatroom.codec;

import cn.dumboj.netty.chatroom.message.protocol.Message;
import cn.dumboj.netty.chatroom.message.type.MessageTypeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 协议消息解码器
 */
@Slf4j
public class ChatByteToMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        // 魔数
        ByteBuf byteBuf = in.readBytes(4);
        log.debug("魔数是：{}", byteBuf.toString(StandardCharsets.UTF_8));

        // 版本号
        byte b = in.readByte();
        log.debug("版本号是：{}", b);

        //序列化方式
        byte serializerType = in.readByte();
        log.debug("序列化方式：{}", serializerType);

        //消息类型
        byte messageType = in.readByte();
        log.debug("消息类型：{}", messageType);

        //消息长度
        int length = in.readInt();
        log.debug("消息长度：{}", length);

        //消息内容
        Message message = null;

        //限制只处理符合规范的协议类型：json
        // byte - > int 自动转化
        if (serializerType == 1) {
            ObjectMapper objectMapper = new ObjectMapper();
            Message msg = objectMapper.readValue(in.readCharSequence(length, StandardCharsets.UTF_8).toString()
                    , MessageTypeMap.typeMaps.get((int) messageType));
            list.add(msg);
        }
    }
}
