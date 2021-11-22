package io.github.kimmking.gateway.outbound.httpclient4;

import io.github.kimmking.gateway.filter.HeaderHttpResponseFilter;
import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.filter.HttpResponseFilter;
import io.github.kimmking.gateway.router.HttpEndpointRouter;
import io.github.kimmking.gateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

// 在 OutboundHandler 里面配置了后端的真实的业务服务的URL地址
// 所有业务请求过来，都直接通过线程池去运行方法 fetchGet()
public class HttpOutboundHandler {

    private CloseableHttpAsyncClient httpclient; // httpclient：异步http请求
    private ExecutorService proxyService; // 异步执行的机制
    private List<String> backendUrls; // 后端网址

    HttpResponseFilter filter = new HeaderHttpResponseFilter(); // 响应过滤器
    HttpEndpointRouter router = new RandomHttpEndpointRouter(); // 路由

    public HttpOutboundHandler(List<String> backends) {

        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());

        // 返回 Java 虚拟机可用的最大处理器数，永远不会小于 1。该值可能会在虚拟机的特定调用期间发生变化。 因此，对可用处理器数量敏感的应用程序应偶尔轮询此属性并适当调整其资源使用。
        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();丢弃策略
        // 使用给定的初始参数创建一个新的线程池 ThreadPoolExecutor
        //     public ThreadPoolExecutor(int corePoolSize,
        //                              int maximumPoolSize,
        //                              long keepAliveTime,
        //                              TimeUnit unit,
        //                              BlockingQueue<Runnable> workQueue,
        //                              ThreadFactory threadFactory,
        //                              RejectedExecutionHandler handler){...}
        // 核心线程池大小、最大线程池大小、线程最大空闲时间、时间单位、线程等待队列、线程创建工厂、拒绝策略
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        // ConnectTimeout : 连接超时,连接建立时间,三次握手完成时间。
        // SocketTimeout : 请求超时,数据传输过程中数据包之间间隔的最大时间。
        // ConnectionRequestTimeout : 使用连接池来管理连接,从连接池获取连接的超时时间。
        // 配置io线程
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        httpclient.start(); // 启动 HttpClient
    }

    private String formatUrl(String backend) {
        // endsWith:测试此字符串是否以指定的后缀结尾。
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }

    // FullHttpRequest：封装http请求，有获取消息体的方法
    //            FullHttpRequest httpRequest = (FullHttpRequest)msg;
    //            String path=httpRequest.uri();          //获取路径
    //            String body = getBody(httpRequest);     //获取参数
    //            HttpMethod method=httpRequest.method();//获取请求方法
    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        System.out.println("handle执行了");
        String backendUrl = router.route(this.backendUrls); // 返回随机的一个后端服务
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx); // 该过滤器主要是向请求头中添加信息
        // 所有业务请求过来，都直接通过线程池去运行方法fetchGet()
        proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
    }

    // 通过 httpClient 执行 httpGet 请求，请求后端真实的业务服务，拿到响应结果数据并封装为body，再把body封装到HttpResponse，再返回给客户端
    // url：后端真实服务
    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {

        final HttpGet httpGet = new HttpGet(url);

        //httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpGet.setHeader("mao", inbound.headers().get("mao"));

        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse endpointResponse) {
                try {
                    handleResponse(inbound, ctx, endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null; // 下面组装 HttpResponse
        try {
//            String value = "hello,kimmking";
//            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
//            response.headers().set("Content-Type", "application/json");
//            response.headers().setInt("Content-Length", response.content().readableBytes());

            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
//            System.out.println(new String(body));
//            System.out.println(body.length);

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            filter.filter(response);

//            for (Header e : endpointResponse.getAllHeaders()) {
//                //response.headers().set(e.getName(),e.getValue());
//                System.out.println(e.getName() + " => " + e.getValue());
//            } 

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
