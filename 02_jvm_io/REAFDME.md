# Week02 作业题目：

> 1.（选做）使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。
>
> 2.（选做）使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。
>
> 3.（选做）如果自己本地有可以运行的项目，可以按照 2 的方式进行演练。
>
> 4.（必做）根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 和堆内存的总结，提交到 GitHub。
>
> 5.（选做）运行课上的例子，以及 Netty 的例子，分析相关现象。
>
> 6.（必做）写一段代码，使用 HttpClient 或 OkHttp 访问  http://localhost:8801 ，代码提交到 GitHub。

本机环境：JDK 8

# 题目1

> https://gceasy.io/gc-index.jsp

## ParallelGC

### -Xms1g -Xmx1g

> java -Xms1g -Xmx1g -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

先执行了12次Yong GC，然后执行了一次Full GC，且共执行了1次FULL GC

解读第一条GC日志记录

> 2021-11-14T16:16:33.167-0800: 0.453: [GC (Allocation Failure) [PSYoungGen: 262144K->43510K(305664K)] 262144K->83447K(1005056K), 0.0375923 secs] [Times: user=0.04 sys=0.08, real=0.04 secs] 

手动分层

> 2021-11-14T16:16:33.167-0800: 
>
> 0.453
>
>  [GC (Allocation Failure) 
>
> ​	[PSYoungGen: 262144K->43510K(305664K)] 
>
> ​	262144K->83447K(1005056K), 0.0375923 secs]
>
>  [Times: user=0.04 sys=0.08, real=0.04 secs] 

**2021-11-14T16:16:33.167-0800**：时间戳，0800 表示东八区

**0.453**：JVM运行时刻

**[]**：总体分为两部分第一个 [] 是 GC 的变化情况，第二个 [] 是 CPU 的使用情况

**GC (Allocation Failure) **：发生 GC 的原因，此处为内存分配失败

**PSYoungGen: 262144K->43510K(305664K)**:Yong区大概从262M被压缩至43M（准确的话，除数为1024K，以下涉及的数字均为截取值），差值为219M，即Yong区回收的空间为219M；Yong区大小约为305M。

**262144K->83447K(1005056K)**：整个堆内存大概从261M压缩至83M；注意因为是第一次GC，此时堆内存使用的空间与Yong区使用的空间相等。而大约有40M（83-43）的空间是Old区对象占用的内存空间

**0.0375923 secs**：ParallelGC执行的时长，约为37毫秒，ParallelGC需要STW

### -Xms512m -Xmx512m

> java -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

最后全部都是FULL GC

### -Xms256m -Xmx256m

> java -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

执行 FULL GC 的占比进一步提高，且程序出现异常 OutOfMemoryError。

```
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:48)
	at GCLogAnalysis.main(GCLogAnalysis.java:25)
```

## SerialGC

### -Xms1g -Xmx1g

> java -Xms1g -Xmx1g -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC GCLogAnalysis

先是9次 YongGC，第10次为Yong GC、Old GC，SerialGC执行GC花费的时间明显比ParallelGC的长

### -Xms512m -Xmx512m

> java -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC GCLogAnalysis

有数次的FULL GC

### -Xms256m -Xmx256m

> java -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC GCLogAnalysis

出现异常

```
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:48)
	at GCLogAnalysis.main(GCLogAnalysis.java:25)
```

## ConcMarkSweepGC

### -Xms1g -Xmx1g

> java -Xms1g -Xmx1g -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC GCLogAnalysis

### -Xms512m -Xmx512m

> java -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC GCLogAnalysis

CMS-concurrent-sweep次数明显提升

### -Xms256m -Xmx256m

> java -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC GCLogAnalysis

## G1GC

### -Xms1g -Xmx1g

> java -Xms1g -Xmx1g -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseG1GC GCLogAnalysis

### -Xms512m -Xmx512m

> java -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseG1GC GCLogAnalysis

### -Xms256m -Xmx256m

> java -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseG1GC GCLogAnalysis

# 题目2

1. 启动进程

> java -jar -Xmx1g -Xms1g -XX:+UseSerialGC gateway-server-0.0.1-SNAPSHOT.jar

```
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   628.36us  458.44us  13.80ms   92.82%
    Req/Sec    27.74k     4.01k   35.45k    74.70%
  1637100 requests in 30.01s, 195.45MB read
Requests/sec:  54543.33
Transfer/sec:      6.51MB
```

![image-20211114233414067](https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/202111142334151.png)

> java -jar -Xmx1g -Xms1g -XX:+UseParallelGC gateway-server-0.0.1-SNAPSHOT.jar

```
unning 30s test @ http://localhost:8088/api/hello
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.26ms   19.20ms 179.44ms   95.99%
    Req/Sec    26.35k     5.46k   34.42k    81.36%
  1558763 requests in 30.04s, 186.10MB read
Requests/sec:  51894.04
Transfer/sec:      6.20MB
```

![image-20211114233744946](https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/202111142337983.png)

> java -jar -Xmx1g -Xms1g -XX:+UseConcMarkSweepGC gateway-server-0.0.1-SNAPSHOT.jar

```
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.18ms   12.75ms 227.02ms   96.03%
    Req/Sec    19.87k     8.91k   36.64k    67.51%
  1176940 requests in 30.07s, 140.52MB read
Requests/sec:  39139.78
Transfer/sec:      4.67MB
```



![image-20211114234009949](https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/202111142340984.png)

> java -jar -Xmx1g -Xms1g -XX:+UseG1GC gateway-server-0.0.1-SNAPSHOT.jar

2. 压测

> wrk -t12 -c40 -d30s http://localhost:8088/api/hello

3. JvisualVm监测

> https://visualvm.github.io/download.html
>
> wrk -c40 -d30s http://localhost:8088/api/hello

