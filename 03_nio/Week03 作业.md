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

**代码地址**

> https://github.com/luffyhub/JavaCourseCodes7/tree/main/02_jvm_io/src/nio_1/netty

调用第 2 周实现的简单HTTP服务器，模拟的后端真实业务

> https://github.com/luffyhub/JavaCourseCodes7/blob/main/02_jvm_io/src/nio_1/HttpServer01.java
>
> https://github.com/luffyhub/JavaCourseCodes7/blob/main/02_jvm_io/src/nio_1/HttpServer02.java

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

> 使用 Netty 实现后端 HTTP 访问（代替上一步骤）



## 题目3

> 按照我给大家提示写的 Filter的接口去实现一个 Filter，比如说对于我们所有的请求做一次过滤，添加一个请求头，再发给我们后端的业务服务，例如我们可以给我们的请求头那里边加一个 xjava这是key——kimmking，然后在我们通过HttpClient或者是Okhttp，请求后端的具体的真实的业务服务的时侯，把这个头带进去



## 题目4

> 让后端可以同时支撑多个业务服务的这种实例，让它们能够做负载均衡

糊里糊涂的，再看看：）

## 题目5



## 题目 6

