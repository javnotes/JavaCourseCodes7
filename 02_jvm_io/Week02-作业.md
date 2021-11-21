# Week02 作业题目：

> **Week02 作业题目：**
>
> **1.（选做）**使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。
>
> **2.（选做）**使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。
>
> **3.（选做）**如果自己本地有可以运行的项目，可以按照 2 的方式进行演练。
>
> **4.（必做）**根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 和堆内存的总结，提交到 GitHub。
>
> **5.（选做）**运行课上的例子，以及 Netty 的例子，分析相关现象。
> **6.（必做）**写一段代码，使用 HttpClient 或 OkHttp 访问 [ http://localhost:8801 ](http://localhost:8801/)，代码提交到 GitHub。

本机环境：JDK 8

# 题目1

> 使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。

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

> 使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。

**指定-Xmx1g -Xms1g，怀疑受本地系统影响，结果看起来并无明显规律：）**

---

压测

> wrk -c40 -d30s http://localhost:8088/api/hello

## SerialGC

>  java -jar -Xmx1g -Xms1g -XX:+UseSerialGC gateway-server-0.0.1-SNAPSHOT.jar

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

## ParallelGC

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

## CMS

> java -jar -Xmx1g -Xms1g -XX:+UseConcMarkSweepGC gateway-server-0.0.1-SNAPSHOT.jar

```
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.16ms   13.78ms 131.27ms   96.29%
    Req/Sec    26.40k     5.43k   56.32k    89.26%
  1571687 requests in 30.07s, 187.64MB read
Requests/sec:  52259.59
Transfer/sec:      6.24MB
```



![image-20211114234009949](/Users/luf/Library/Application%20Support/typora-user-images/image-20211114234009949.png)

## G1GC

> java -jar -Xmx1g -Xms1g -XX:+UseG1GC gateway-server-0.0.1-SNAPSHOT.jar

```
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   845.26us    0.87ms  35.45ms   95.85%
    Req/Sec    20.99k     4.55k   30.65k    64.33%
  1254260 requests in 30.06s, 149.75MB read
Requests/sec:  41721.96
Transfer/sec:      4.98MB
```

![](https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/202111142351785.png)

# 题目3

> 如果自己本地有可以运行的项目，可以按照 2 的方式进行演练。

暂时跳过

# 题目4

> 根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 和堆内存的总结，提交到 GitHub。

## 串行 GC

特点：单线程执行，应用需要暂停。

只适合几百 MB 堆内存的单核 CPU 的 JVM。因为是单线程处理垃圾回收，会触发全线暂停（STW），即停止所有的应用线程，如果内存较大，垃圾回收的时间就会较长，效率比较低，导致整个业务系统暂停的时间特别长，不能充分利用多核 CPU。

## 并行 GC

设计目标：为了最大化的增加整个系统的业务处理吞吐量

特点：多线程并行地执行垃圾回收，但是在多线程 GC 线程执行的时候，需要暂停当前的业务线程来执行垃圾回收。

并行垃圾收集器适用于多核服务器，在 GC 期间，所有 CPU 内核都在并行清理垃圾，所以总暂停时间更短。在两次 GC 周期的间隔期，没有 GC 线程在运行，不会消耗任何系统资源。对系统资源的有效使用，能达到更高的吞吐量。

## CMS GC

设计目标：避免在老年代垃圾收集时出现长时间的卡顿。

把整个垃圾回收的过程分成了很多个阶段，其中大部分的阶段可以用和业务线程并发执行的方式来执行垃圾回收工作，其他阶段GC线程的执行与业务线程并发地执行，对业务的影响比较小。但是整体的吞吐量一般会比并行GC 要差，因为它不能保证所有的 CPU 和线程资源在这一段的时间内用来做 GC。

## G1GC

通过划分多个内存区域做增量整理和回收，进一步降低延迟；

G1 在注重低延迟的同时，吞吐量上面也有很好的表现。

注意的是虽然可以进一步的降低单次的垃圾回收的延迟，但是它的缺点就是在某些条件下有可能会产生FULL GC 进一步的导致 GC 的退化，退化成串行化 GC。

# 题目5

> 运行课上的例子，以及 Netty 的例子，分析相关现象。

对Netty暂不了解，入门后补上作业.......

# 题目6

>  写一段代码，使用 HttpClient 或 OkHttp 访问 [ http://localhost:8801 ](http://localhost:8801/)，代码提交到 GitHub。
