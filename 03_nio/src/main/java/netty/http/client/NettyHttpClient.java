package netty.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 最简化的 NettyHttpServer Demo
 * @author: luf
 * @date: 2021/11/24
 **/
public class NettyHttpClient {
    // 失败重连最大次数
    private static final int MAX_RETRY = 5;
    // 服务端IP
    private static final String HOST = "127.0.0.1";
    // 请求端口
    private static final int PORT = 8808;

    public static void main(String[] args) {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new HttpInitializer());

        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }


    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port)
                .addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("【NettyHttpClient】：连接成功！");
                    } else if (retry == 0) {
                        System.err.println("【NettyHttpClient】：重试次数(" + MAX_RETRY + ")已用完，放弃连接！");
                    } else {
                        // 本次重试是第几次尝试连接
                        int order = (MAX_RETRY - retry) + 1;
                        // 本机重连的间隔时间，第一次等1秒，第2次等2秒，第三次等4秒
                        int delay = 1 << order;
                        System.err.println("【NettyHttpClient】：" + new Date() + "：连接失败，第" + order + "次重连...");
                        bootstrap.config().group().schedule(() -> connect(bootstrap, HOST, PORT, retry - 1), delay, TimeUnit.SECONDS);
                    }
                });
    }
}
