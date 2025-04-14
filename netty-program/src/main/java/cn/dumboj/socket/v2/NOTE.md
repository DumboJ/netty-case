# 代码说明

### 1. 该代码中已经解决的问题。v1版本中代码取消 链接阻塞和io读写阻塞后，空转问题导致的资源浪费
        思路：引入Java NIO 的 Selector 模型 实现阻塞，selector.select() 监听特定事件（connect/read/write/accept）发生时才取消阻塞
### 2. selector 多路复用，单线程处理多个事件。
    
        1. Selector 对象中两个重要集合 ：
                public abstract Set<SelectionKey> keys();

                public abstract Set<SelectionKey> selectedKeys();//发生的 实际事件
        代码中注意：获取selectionKey的集合后，在处理时，需要获取到对应的事件后，remove出当前selected集合，否则会影响后续流程。比如：有连接事件和读事件，会一直迭代获取到第一个selectionKey
             
        2. 注意将 ByteBuffer 作为附件与 channel 绑定，多次时不会发生读取错误
        3. ！！发生半包和粘包情况下需要考虑ByteBuffer扩缩容问题--Netty 中 ByteBuf 动态扩缩容
        4. ！！扩容后考虑扩容前后的数据拷贝问题，传统的数据拷贝效率低--解决方案：零拷贝
        5. 写模式下往客户端写大量内容时，如果不监听处理写事件，会有空写