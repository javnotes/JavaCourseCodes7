package netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 最简化的 NettyHttpServer Demo
public class NettyHttpServer {
    // 入口
    public static void main(String[] args) throws InterruptedException {
        // 指定服务端要监听端口
        int port = 8808;
        // 监听端口，accept 新连接的线程组，bossGroup接收完请求连接，扔给workerGroup去处理。
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        // 处理每一条连接的数据读写的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        try {
            // 服务端引导类，引导我们进行服务端的启动工作，整个NettyHttpServe启动r的入口点
            ServerBootstrap b = new ServerBootstrap();
            // 绑定chanel的各种参数，设置TCP底层属性
            b.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .childOption(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // group() 给引导类配置两大线程组，确定线程模型
            b.group(bossGroup, workerGroup)
                    // 指定IO模型
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 服务端相关的数据处理逻辑就是通过 ServerBootstrap 的 childHandler() 方法指定
                    // 定义后续每条连接的数据读写，业务处理逻辑，
                    // HttpInitializer 就是我们封装的类 HttpInitializer extends ChannelInitializer<SocketChannel>
                    .childHandler(new HttpInitializer());

            // 绑定端口，开启channel，服务器就启动了
            Channel ch = b.bind(port).sync().channel();
            // 开启netty http服务器，监听地址和端口为 http://127.0.0.1:8808/
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
