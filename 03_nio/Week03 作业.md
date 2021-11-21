# Week03 作业

> 基础代码可以 fork： [ https://github.com/kimmking/JavaCourseCodes](https://github.com/kimmking/JavaCourseCodes)02nio/nio02 文件夹下，实现以后，代码提交到 GitHub。
>
> 1.（必做）整合你上次作业的 httpclient/okhttp。
> 2.（选做）使用 Netty 实现后端 HTTP 访问（代替上一步骤）。
> 3.（必做）实现过滤器。
> 4.（选做）实现路由。
> 5.（选做）跑一跑课上的各个例子，加深对多线程的理解。
> 6.（选做）完善网关的例子，试着调整其中的线程池参数。

---

## 题目1

> 整合上次作业的 HttpClient

代码地址

> 

1. 根据基础代码，主要是修改类 **nio_1.netty.HttpHandler** ，将逻辑改为：

```java
if (uri.contains("/test")) {
    // 相当于是业务处理代码，再调用后端真实的业务服务
    handlerTest(fullRequest, ctx, "http://localhost:8801");
} else { // 非/test的
    handlerTest(fullRequest, ctx, "http://localhost:8802");
}
```

2. 整合 HttpClient

```java
private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String url {
    String value = null;
    // 组装HttpResponse对象
    FullHttpResponse response = null;

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet httpGet = new HttpGet(url);
    CloseableHttpResponse backResponse = null;
    
    try {
        backResponse = httpClient.execute(httpGet);
        value = EntityUtils.toString(backResponse.getEntity());
        ...
        }
    ...
}
```

3. 启动 HttpServer01、HttpServer02、NettyHttpServer，测试：

```bash
curl http://localhost:8808/test
hello,nio1%
```

```bash
curl http://localhost:8808
hello,nio2%
```

## 题目2





## 题目3







## 题目4







## 题目5







## 题目 6





