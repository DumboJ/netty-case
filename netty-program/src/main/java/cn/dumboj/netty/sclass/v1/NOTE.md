Netty 基础 服务端客户端的实现，此实现中 Server 端的 NioEventLoopGroup 未区分监听accept事件 和 IO 的操作分发。
统一由一组 NioEventLoop (work) 来处理

##### 注意点：
- 源码中创建 NioEventLoopGroup 的实现时核心线程数是未设置时依赖 操作系统核心 * 2 来作为线程初始容量的
    - private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    - protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
    }
- DefaultEventLoop 
    `` 一个普通的线程，内容工作可以由程序员决定，他不做 IO监控 读写的处理 ``
- NioEventLoop
    `` IO Write Read 事件监控 ``