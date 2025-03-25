# netty-case
learning about netty

## Modules

***  

### 1. Java NIO program 
  - Channel
    - FileChannel
      - create Channel : 
        - new FileInputStream/FileOutputStream(path).getChannel();
        - new RandomAccessFile(path,ModeOfReadWrite).getChannel()
        - java.nio.channels.FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)
    - TCP/IP Channel 
      - ServerSocketChannel (server)
      - SocketChannel (client)
    - UDP Channel
      - DatagramChannel
    - 文件读写
      - FileChannel.write()
      - FileChannel.read()
      - FileChannel.transferTo(0,length,targetFileChannel)
  - ByteBuffer
    - 抽象类
    - 操作内容依赖它缓冲完成读写，本身没有方向性，需视情况调整读写模式,创建后默认写模式
    - 获取方式
      - ByteBuffer.allocate(int size);
      - encode
    - 父接口 Buffer中的几个重要成员变量--读写模式的调整都是依赖这几个成员变量的值实现 *i.e:compact写模式时设置position=limit*
      - capacity  
      - position
      - limit
    - 读模式
      - flip()
    - 写模式
      - compact()
      - clear()
    - 常见API
      - read()
      - write()
      - rewind() 复读数据,将position设置为0
      - mark/reset mark标记position位置,reset可跳回标记位置读

### 2. Netty NIO program
