package netty.http.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

// 继承了 Channel 的初始化
public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    // 给服务端添加一个逻辑处理器.addLast()，这个处理器的作用就是负责读取客户端来的数据
    public void initChannel(SocketChannel ch) {
        // ch.pipeline()返回的是和这条连接相关的逻辑处理链，采用了责任链模式；在这次网络处理中，需要我们控制它的流水线流程的这部分
        // 获取服务端侧关于这条连接的逻辑处理链 pipeline，然后添加一个逻辑处理器，负责读取客户端发来的数据
        //
        //
        ChannelPipeline p = ch.pipeline();
        // 添加HttpServer编码器
        p.addLast(new HttpServerCodec());
        //p.addLast(new HttpServerExpectContinueHandler());
        // 添加报文聚合器
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        // 添加一个逻辑处理器，在客户端建立连接后，
        p.addLast(new HttpHandler());
    }
}
