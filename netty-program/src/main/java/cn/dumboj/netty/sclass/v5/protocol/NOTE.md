### Netty 中通信协议与编解码器说明
#### 1. 编解码器
- 编码器 OutBoundHandler出站操作 PipeLine传播方向 TailContext -> HeadContext
  - 一级编码器
    - MessageToByteEncoder 
        ` 将消息转化为ByteBuf传递`
  - 二级编码器
    - MessageToMessageEncoder 
    `将pipeline中Message数据对象转为其它类型,I只限制输入类型，根据实现类的encode方法为准，出站操作会由当前context向前传递到HeadContext向外发送，但发送前必须由MessageToByteEncoder编码为ByteBuf对象（当然也可以在自身实现类的encode中编码成ByteBuf），使用时多搭配泛型使用`
- 解码器 InBoundHandler 入站操作，PipeLine传播方向 HeadContext -> TailContext
  - 一级解码器
    - ByteToMessageDecoder 
      `将ByteBuf转化为消息对象`
  - 二级解码器
    - MessageToMessageDecoder
      `将pipline中Message对象转化为其它目标类型，I限制传入对象类型，encode方法实际解码为什么类型看具体业务。需要注意的是与MessageToMessageEncoder不同的是，它们的传播方向。`
#### 2.几种内置的编解码器
  - ObjectEncoder/ObjectDecoder  
    - `Java对象序列化/反序列化 传送对象必须实现 Serializable 接口才能发送接收成功`
    - 并且该编码器严格限定输入类型Serializable `public class ObjectEncoder extends MessageToByteEncoder<Serializable>`
  - JsonObjectDecoder `没有匹配的Encoder，JSON本身为字符串直接编码为ByteBuf就行，不需要特殊处理`