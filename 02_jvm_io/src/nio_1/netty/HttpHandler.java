package nio_1.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

// HttpHandler：整个Nettyserver启动以后，客户端的请求进来时，读取客户端请求的Handler
public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    // 可以通过客户端连接 Netty的这个通道里直接读到我们的数据
    // msg 表示这次请求的所有的数据包装类的这样一个对象，Http协议的报文信息
    // 把msg转型成一个 Httprequest 对象就可以拿到它内部的结构
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            String uri = fullRequest.uri();
            //logger.info("接收到的请求url为{}", uri);
            // 相当于时路由
            if (uri.contains("/test")) {
                // 相当于是业务处理代码，再调用后端真实的业务服务
                handlerTest(fullRequest, ctx, "http://localhost:8801");
            } else { // 非/test的
                handlerTest(fullRequest, ctx, "http://localhost:8802");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    // 给客户端发送报文
    // 对接上次作业的httpclient或者okhttp请求另一个url的响应数据，拿到响应数据并通过value进行返回至前端
    // 这就相当于代理了我们客户端的求和我们后端的真实的业务服务url
    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String url) {

        String value = null;

        // 组装HttpResponse对象
        FullHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse backResponse = null;

        try {
            backResponse = httpClient.execute(httpGet);
            // System.out.println(EntityUtils.toString(response.getEntity()));
            value = EntityUtils.toString(backResponse.getEntity());
//            httpGet ...  http://localhost:8801
//            返回的响应，"hello,nio";
//            value = reponse....

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (Exception e) {
            System.out.println("处理出错:" + e.getMessage());
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
                ctx.flush();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
