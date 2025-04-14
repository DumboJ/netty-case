### EventLoopGroup 分类的 Netty 服务端客户端实现
 - 基于 Boss Work 的编程模型来对 Accept 和 IO 操作使用不同的 EventLoopGroup,通过默认实现创建 NioEventLoopGroup 
 - Boss 单线程主要负责连接处理
 - Work 多线程主要负责IO操作