### Netty关于生命周期的回调
#### 1.创建回调监听 override ChannelInboundHandlerAdapter的成员方法
 ~~~java
   //  省略 ServerBootStrap 创建代码
    .childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast(new LoggingHandler())
                    .addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    /**
                     * 自定义关于建立连接的SocketChannel的入站处理器ChannelInboundHandler的生命周期回调
                     * */
                    .addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        // 各种类型的回调器
                    }
        }
    });
 ~~~

#### 2.回调重载方法类型、触发时机及使用场景说明
> 自定义回调监听channelRead(SocketChannelContext ch,Object msg) 时要注意 msg 资源的释放，避免内存泄露 / instance of ByteBuf时一定要释放，但当添加了StringDecoder或者使用SimpleChannelInboundHandler\<T> 时，该处理器封装会释放资源

| 方法                                                              | 触发时机                     | 使用场景  |
|------------------------------------------|-------------------|----------------------------------------------------------|
| channelRegistered    | Channel被注册到EventLoop时触发 | 初始化资源/设置属性、注册监听器、日志记录等  |
| channelActive         |  Channel 激活（连接建立或绑定完成）时触发         | 适用于连接池、会话管理等场景/发送欢迎消息、开始读取数据(登录认证信息发送)、心跳启动等|
| channelRead          | 每次读取到入站数据时触发         | 处理业务逻辑、协议解析、数据转发等/通常结合 SimpleChannelInboundHandler 来简化类型转换和释放资源 |
| channelReadComplete   | Channel数据读取完毕时触发        | 执行 flush、减少频繁 flush 提高性能 |
| channelInactive       | Channel断开连接时触发  | 记录断开时间、用户离线、资源释放、清理状态、通知上层服务等/在线人数统计、重连机制|
| channelUnregistered  | Channel 从 EventLoop 取消注册时触发 | 清理资源、释放连接信息等|
|  userEventTriggered  |     用户自定义事件被触发时调用 | 实现超时断开机制、空闲检测（心跳机制）、自定义事件分发等 |
| exceptionCaught      |  发生异常时触发          | 异常捕获、日志打印、连接关闭、错误处理等|
|  channelWritabilityChanged|Channel 的可写状态发生变化时触发| 控制流量、防止内存溢出、恢复发送队列等 |

#### 3.心跳功能的实现
> IdleStateHandler 实现心跳功能，在指定的时间段内没有收到数据时触发 ，配合回调 userEventTriggered实现
~~~java
 // IdleStateHandler 默认是检测读写空闲，也可以设置检测读空闲或者写空闲，具体设置方式如下：
 // IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds)
~~~