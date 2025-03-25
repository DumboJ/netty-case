package cn.dumboj.basic.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessFileChannel {
    /**
     * 文件系统的 channel 创建方式
     * new RandomAccessFile(path,ModeOfReadWrite).getChannel()
     */
    public static void main(String [] args) {

        String filePath = "D:\\Coding\\learing\\Java\\Netty\\netty-case\\netty-program\\src\\main\\resources\\FileNioText.txt";

        try (FileChannel fileChannel = new RandomAccessFile(filePath,"rw").getChannel()) {
            //初始化 ByteBuffer,分配大小后不可动态扩容
            ByteBuffer bf = ByteBuffer.allocate(10);

            while (true) {
                //读取文件内容
                int read = fileChannel.read(bf);//buff 容量限制
                if (read == -1) break;

                //模式切换
                bf.flip();

                while (bf.hasRemaining()) {
                    System.out.println((char) bf.get());
                }
                //ByteBuffer 切换成写模式
                bf.clear();
            }
        } catch (FileNotFoundException e) {
            //ignore IOStream cause
        } catch (IOException e) {
            //ignore ByteBuffer cause
        }
    }
}
