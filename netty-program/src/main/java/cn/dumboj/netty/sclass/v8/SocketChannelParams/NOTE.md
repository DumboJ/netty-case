### Netty SocketChannel 的常用参数配置
#### 1. 支持可配置的类型
|  参数名称 | 参数作用                                       | 影响范围                             |
| --- |--------------------------------------------|----------------------------------|
|ServerSocketChannelOption| 配置**服务端监听** socket(ServerSocketChannel) 的参数 | 主要影响连接建立阶段的行为（如连接队列、接收缓冲区等）      |
|SocketChannelOption| 配置**每一个客户端连接** socket(SocketChannel) 的参数   | 主要影响的数据传输阶段的特性（如缓冲区分配策略、TCP 特性等） | 

#### 2. 配置参数详细说明
1.option 参数配置（监听 socket，影响连接建立阶段行为 ）
> .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false)) // 强制使用 Heap Buffer,不一定使用系统参数

> 系统参数也可以配置 直接内存还是堆内存 io.netty.noPreferDirect = false 使用直接内存 true 使用堆内存

|  参数名称|类型|默认值| 说明                |使用场景|
| --- | --- | --- |-------------------| --- |
|SO_BACKLOG|int|平台默认通常128| 设置请求连接的全连接队列大小    |提高并发连接能力时可增大该值（如设置为 1024）|
|RCVBUF_ALLOCATOR|RecvByteBufAllocator|AdaptiveRecvByteBufAllocator.DEFAULT| 接收缓冲区分配策略**可被继承** |控制接收缓冲区大小，提高吞吐量或减少内存浪费|
|SO_REUSEADDR|boolean|false| 允许重复使用本地地址和端口 | 允许一个进程绑定到同一个地址和端口，但允许多个进程绑定到同一个地址和端口 |
|CONNECT_TIMEOUT_MILLIS|int|30000| 连接超时时间 | 配置连接建立超时时间，防止连接建立失败时消耗过多资源 |

2.childOption 参数配置（每个新连接的 socket行为），影响实际IO

|参数名称|类型|默认值| 说明|使用场景|
|---|---|---|-------------------|---|
|TCP_NODELAY|boolean|false| 禁用Nagle算法 | 禁用Nagle算法，提高实时性 |
|SO_KEEPALIVE|boolean|false| 启用心跳机制 | 启用心跳机制，保持连接可用性 |
|SO_SNDBUF|int|平台默认通常8192| 发送缓冲区大小 | 控制发送缓冲区大小，提高吞吐量或减少内存浪费 |
|SO_RCVBUF|int|平台默认通常8192| 接收缓冲区大小 | 控制接收缓冲区大小，提高吞吐量或减少内存浪费 |
|ALLOCATOR|ByteBufAllocator|PooledByteBufAllocator.DEFAULT| 缓冲区分配策略**不可被继承** | 控制缓冲区分配策略，提高内存利用率 |
|WRITE_BUFFER_WATER_MARK|WriteBufferWaterMark|WriteBufferWaterMark.DEFAULT| 写缓冲区水位线 | 控制写缓冲区水位线，提高吞吐量或减少内存浪费 |
|MESSAGE_SIZE_ESTIMATOR|MessageSizeEstimator|DefaultMessageSizeEstimator.DEFAULT| 消息大小估计器 | 控制消息大小估计器，提高内存利用率 |
|WRITE_SPIN_COUNT|int|16| 写空闲次数 | 控制写空闲次数，提高吞吐量或减少内存浪费 |
|ALLOW_HALF_CLOSURE|boolean|false| 允许半关闭 | 允许半关闭，允许客户端关闭连接但服务端继续写入数据 |
|AUTO_READ|boolean|true| 自动读取数据 | 自动读取数据，减少手动读取数据 |
|WRITE_BUFFER_HIGH_WATER_MARK|int|32 * 1024| 写缓冲区高水位线 | 控制写缓冲区高水位线，提高吞吐量或减少内存浪费 |
|SINGLE_EVENT_EXECUTOR_PER_GROUP|boolean|false| 单一事件执行器 | 单一事件执行器，提高性能 |

3.参数选择建议

|场景|推荐配置项|
| --- | --- |
|高并发连接|SO_BACKLOG=1024, RCVBUF_ALLOCATOR 自定义|
|高并发读写|SO_SNDBUF=64 * 1024, SO_RCVBUF=64 * 1024, ALLOCATOR 自定义, WRITE_BUFFER_WATER_MARK 自定义, WRITE_SPIN_COUNT=64, SINGLE_EVENT_EXECUTOR_PER_GROUP=true|
|实时性|TCP_NODELAY=true, AUTO_READ=false|
|大文件传输|SO_RCVBUF=256KB, SO_SNDBUF=256KB|
|长连接管理|SO_KEEPALIVE=true, ALLOW_HALF_CLOSURE=true, AUTO_READ=true|
|短连接管理|SO_KEEPALIVE=false, ALLOW_HALF_CLOSURE=false, AUTO_READ=false|
|内存敏感|RCVBUF_ALLOCATOR 限制最大分配大小，避免 OOM|