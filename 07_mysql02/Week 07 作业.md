> **1.（选做）**用今天课上学习的知识，分析自己系统的 SQL 和表结构
> **2.（必做）**按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率
>
> **3.（选做）**按自己设计的表结构，插入 1000 万订单模拟数据，测试不同方式的插入效
> **4.（选做）**使用不同的索引或组合，测试不同方式查询效率
> **5.（选做）**调整测试数据，使得数据尽量均匀，模拟 1 年时间内的交易，计算一年的销售报表：销售总额，订单数，客单价，每月销售量，前十的商品等等（可以自己设计更多指标）
> **6.（选做）**尝试自己做一个 ID 生成器（可以模拟 Seq 或 Snowflake）
> **7.（选做）**尝试实现或改造一个非精确分页的程序
>
> **8.（选做）**配置一遍异步复制，半同步复制、组复制
> **9.（必做）**读写分离 - 动态切换数据源版本 1.0
> **10.（必做）**读写分离 - 数据库框架版本 2.0
> **11.（选做）**读写分离 - 数据库中间件版本 3.0
> **12.（选做）**配置 MHA，模拟 master 宕机
> **13.（选做）**配置 MGR，模拟 master 宕机
> **14.（选做）**配置 Orchestrator，模拟 master 宕机，演练 UI 调整拓扑结构

---

# 必做题1

> 按自己设计的表结构，插入 100 万订单模拟数据，测试不同批量插入方式的插入效率（最好在10s内,6s合格）
>
> 群里大佬：线程池多线程 + Java8 Stream parallel 切割2500条 + JDBC参数 rewriteBatchedStatements

## JDBC方式

> https://github.com/luffyhub/JavaCourseCodes7/tree/main/07_mysql02

<img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220001737331.png" alt="image-20211220001737331" style="zoom:50%;" />

# 必做题2

> **读写分离 - 动态切换数据源版本 1.0
>
> 基于 Spring Abstractrouting Data Source 的 DataSource

1数据库版本：MySQL 8.0.27

## 配置主从数复制

> https://github.com/luffyhub/JavaCourseCodes7/blob/main/07_mysql02/%E9%85%8D%E7%BD%AEMySQL%E4%B8%BB%E4%BB%8E%E6%95%B0%E6%8D%AE%E5%BA%93.md



**10.（必做）**读写分离 - 数据库框架版本 2.0

> 基于 Shardingsphere-」DBC配置一个这样的读写分离的demo
>
> 看官网 Github的代码仓库里面 Example里面

