package netty.http.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @description: TODO 类描述
 * @author: luf
 * @date: 2021/11/24
 **/
public class HttpHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 【NettyHttpClient】写出数据");

        // 先获取数据
        ByteBuf buffer = getByteBuf(ctx);
        // 再写数据
        ctx.channel().writeAndFlush(buffer);

    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        byte[] bytes = "【NettyHttpClient】发送数据：Hello, luf~".getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        return buffer;
    }
}
