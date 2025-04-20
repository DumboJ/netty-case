package cn.dumboj.netty.lagou.part4.Bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

/**
 * 认识 ByteBuf 相对于 NIO 中的优化
 * {@link io.netty.buffer.ByteBuf}
 * */
public class TestByteBuf {
    public static void main(String[] args) {
        scanByteBufEnsure();
        typeOfByteBuf();
        sliceByteBuf();
    }

    /**
     * 演示ByteBuf对于slice()方法的操作
     */
    private static void sliceByteBuf() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'});

        System.out.println(buf);
        System.out.println(ByteBufUtil.prettyHexDump(buf));

        //slice() 只是在原始ByteBuf基础上改变写指针。最大容量 maxCapacity 只是原始的可读取字节数
        ByteBuf s1 = buf.slice(0, 6);
        //UnpooledSlicedByteBuf(ridx: 0, widx: 6, cap: 6/6, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 11, cap: 16))
        System.out.println(s1);
        //此处写看是否改变原始ByteBuf
        s1.setByte(5,'g');
        s1.retain();
        ByteBuf s2 = buf.slice(6, 4);
        System.out.println(s2);
        s2.retain();

        //此处释放后，因为slice()操作后的ByteBuf与原始对象共享内存，再获取对象读时原始内存释放会处罚异常
        //解决方法，为slice()的内存空间添加引用计数，避免释放，后续可正常获取
        buf.release();
        System.out.println(ByteBufUtil.prettyHexDump(s1));
        //set之后读一次，证明会影响原始的ByteBuf
        System.out.println(ByteBufUtil.prettyHexDump(buf));
        System.out.println(ByteBufUtil.prettyHexDump(s2));
    }


    /**
     * ByteBuf 类型 ：堆内存/直接内存
     * */
    private static void typeOfByteBuf() {
        ByteBuf directMemory = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("directMemory = " + directMemory);
        ByteBuf heapMemory = ByteBufAllocator.DEFAULT.heapBuffer();
        System.out.println("heapMemory = " + heapMemory);
    }

    private static void scanByteBufEnsure() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeByte('a');
        byteBuf.writeInt(10);//4字节
        byteBuf.writeInt(20);
        byteBuf.writeInt(30);
        System.out.println(ByteBufUtil.prettyHexDump(byteBuf));
    }
}
