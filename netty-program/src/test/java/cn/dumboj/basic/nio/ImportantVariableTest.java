package cn.dumboj.basic.nio;

import java.nio.ByteBuffer;

/**
 * 读写模式和几个常见api对ByteBuffer成员变量的影响
 * */
public class ImportantVariableTest {
    public static void main(String[] args) {
        ByteBuffer bytebuffer = ByteBuffer.allocate(10);
        System.out.println(bytebuffer.position());//0
        System.out.println(bytebuffer.limit());//10
        System.out.println(bytebuffer.capacity());//10

        bytebuffer.put("abcd".getBytes());

        System.out.println(bytebuffer.position());//4
        System.out.println(bytebuffer.limit());//10
        System.out.println(bytebuffer.capacity());//10

        //读模式
        bytebuffer.flip();

        System.out.println(bytebuffer.position());//0
        System.out.println(bytebuffer.limit());//4
        System.out.println(bytebuffer.capacity());//10
    }
}
