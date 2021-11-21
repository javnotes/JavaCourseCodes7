package nio_1.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

// 继承了 Channel 的初始化
public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        // pipeline：在这次网络处理中，需要我们控制它的流水线流程的这部分
        ChannelPipeline p = ch.pipeline();
        // 添加HttpServer编码器
        p.addLast(new HttpServerCodec());
        //p.addLast(new HttpServerExpectContinueHandler());
        // 添加报文聚合器
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        p.addLast(new HttpHandler());
    }
}
