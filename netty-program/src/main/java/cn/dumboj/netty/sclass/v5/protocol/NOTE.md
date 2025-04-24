### Netty 中通信协议与编解码器说明
#### 1. 编解码器
1. [x] `ByteToMessage 与 MessageToMessage 的区别:一级编解码器会对数据报文进行缓存，二级编解码器不会，二级编解码器只是对于处理类型的转换`

- 编码器 OutBoundHandler出站操作 PipeLine传播方向 TailContext -> HeadContext
  - 一级编码器
    - MessageToByteEncoder 
    1. [x] 将消息转化为ByteBuf传递
    2. [x] 不需要关注拆包/粘包问题
    3. [x] MessageToByteEncoder 重写了 ChanneOutboundHandler 的 write() 方法
    ~~~java
        1. acceptOutboundMessage 判断是否有匹配的消息类型，如果匹配需要执行编码流程，如果不匹配直接继续传递给下一个 ChannelOutboundHandler；
    
        2. 分配 ByteBuf 资源，默认使用堆外内存；
    
        3. 调用子类实现的 encode 方法完成数据编码，一旦消息被成功编码，会通过调用 ReferenceCountUtil.release(cast) 自动释放；
    
        4. 如果 ByteBuf 可读，说明已经成功编码得到数据，然后写入 ChannelHandlerContext 交到下一个节点；如果 ByteBuf 不可读，则释放 ByteBuf 资源，向下传递空的 ByteBuf 对象
    ~~~
        
  - 二级编码器
    - MessageToMessageEncoder 
    `将pipeline中Message数据对象转为其它类型,I只限制输入类型，根据实现类的encode方法为准，出站操作会由当前context向前传递到HeadContext向外发送，但发送前必须由MessageToByteEncoder编码为ByteBuf对象（当然也可以在自身实现类的encode中编码成ByteBuf），使用时多搭配泛型使用`
- 解码器 InBoundHandler 入站操作，PipeLine传播方向 HeadContext -> TailContext
  - 一级解码器
    - ByteToMessageDecoder 
      1. [x] `由于 TCP 粘包问题，ByteBuf 中可能包含多个有效的报文，或者不够一个完整的报文。Netty 会重复回调 decode() 方法`
      2. [x] `ByteToMessageDecoder 还定义了 decodeLast() 方法;decodeLast 在 Channel 关闭后会被调用一次，主要用于处理 ByteBuf 最后剩余的字节数据.有默认实现，特殊需求可重写该方法扩展`
      3. [x] `将ByteBuf转化为消息对象`
      4. [x] ByteToMessageDecoder 还有一个抽象子类是 ReplayingDecoder。封装了缓冲区的管理，在读取缓冲区数据时，你无须再对字节长度进行检查。因为如果没有足够长度的字节数据，ReplayingDecoder 将终止解码操作;但性能相比较差，不推荐使用
  - 二级解码器
    - MessageToMessageDecoder
      `将pipline中Message对象转化为其它目标类型，I限制传入对象类型，encode方法实际解码为什么类型看具体业务。需要注意的是与MessageToMessageEncoder不同的是，它们的传播方向。`

#### 2.几种内置的编解码器
  - ObjectEncoder/ObjectDecoder  
    - `Java对象序列化/反序列化 传送对象必须实现 Serializable 接口才能发送接收成功`
    - 并且该编码器严格限定输入类型Serializable `public class ObjectEncoder extends MessageToByteEncoder<Serializable>`
  - JsonObjectDecoder `没有匹配的Encoder，JSON本身为字符串直接编码为ByteBuf就行，不需要特殊处理`
  - StringEncoder/StringDecoder|LineDecoder 
  - 复合类型的编解码器 
    - ByteToMessageCodec<I> extends ChannelDuplexHandler  同时支持编解码操作的出入站处理器
    - HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> 支持Http请求响应的编解码器处理器
#### 4.自定义通信协议的规范
```java
+---------------------------------------------------------------+

| 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |

+---------------------------------------------------------------+

| 状态 1byte |        保留字段 4byte     |      数据长度 4byte     | 

+---------------------------------------------------------------+

|                   数据内容 （长度不定）                          |

+---------------------------------------------------------------+
```
