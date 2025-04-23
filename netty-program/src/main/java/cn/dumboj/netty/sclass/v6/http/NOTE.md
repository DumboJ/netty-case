### Netty 关于 Http 请求响应的封装
    `对于 Http 请求响应经过 HttpServerCodec 的编解码，该对象是个复合的编解码器处理器
    extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder>`
#### 1. 普通类型的 入站处理器处理时需自行识别 请求的头体内容做处理 可以自定义按照 头/体 类型来做处理
- ChannelInboundHandlerAdapter

#### 2. Netty 封装的 后续处理器只能单一处理 Http 相关的单一 头/体
-  SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter
- HttpRequest 单一类型
    `普通类型handler基础上Netty加了一层封装，主要把入站的处理类型限制转化成泛型类型，处理特定类型的对象`
#### 3.Netty 更高维度的封装，多类特定类型的解码器， HttpObjectAggregator 实际为 MessageToMessage，聚合了多类型，后续SimpleChannelInboundHandler的处理器 内容可以同时处理头体信息和其它Http相关的内容
- HttpObjectAggregator extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage>
- FullHttpRequest 包含头体多种 Http 内容

#### Servlet J2ee的实现，在Http应用层的封装，另外一种实现，操作请求响应的 API 更简单