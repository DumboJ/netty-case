package cn.dumboj.netty.sclass.v5.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;
/**
 * 支持编解码，出入站都处理的Handler
 * extends ChannelDuplexHandler 符合类型的处理器
 * */
public class ByteToMessageCodecDemo extends ByteToMessageCodec<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {

    }
}
