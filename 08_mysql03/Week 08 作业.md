> **1.（选做）**分析前面作业设计的表，是否可以做垂直拆分。  
> **2.（必做）**设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。  
> **3.（选做）**模拟 1000 万的订单单表数据，迁移到上面作业 2 的分库分表中。  
> **4.（选做）**重新搭建一套 4 个库各 64 个表的分库分表，将作业 2 中的数据迁移到新分库。
> 
> **5.（选做）**列举常见的分布式事务，简单分析其使用场景和优缺点。  
> **6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。  
> **7.（选做）**基于 ShardingSphere narayana XA 实现一个简单的分布式事务 demo。  
> **8.（选做）**基于 seata 框架实现 TCC 或 AT 模式的分布式事务 demo。  
> **9.（选做☆）**设计实现一个简单的 XA 分布式事务框架 demo，只需要能管理和调用 2 个 MySQL 的本地事务即可，不需要考虑全局事务的持久化和恢复、高可用等。  
> **10.（选做☆）**设计实现一个 TCC 分布式事务框架的简单 Demo，需要实现事务管理器，不需要实现全局事务的持久化和恢复、高可用等。  
> **11.（选做☆）**设计实现一个 AT 分布式事务框架的简单 Demo，仅需要支持根据主键 id 进行的单个删改操作的 SQL 或插入操作的事务。

# 必做题1

**2.（必做）**设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。

注意：此次演示中的数据库是在同一MySQL实例中的。

## 建库建表

```sql
show schemas;
create schema demo_ds_0;
create schema demo_ds_1;
show schemas;
```

<img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211227001243424-20211227002349522.png" alt="image-20211227001243424" style="zoom:50%;" />

```sql
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_0 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_1 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_0 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_1 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));


CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_2 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_3 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_4 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_5 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_6 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_7 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_8 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_9 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_10 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_11 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_12 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_13 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_14 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_0.t_order_15 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));


CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_2 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_3 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_4 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_5 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_6 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_7 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_8 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_9 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_10 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_11 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_12 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_13 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));

CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_14 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS demo_ds_1.t_order_15 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
```

<img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211227001427609-20211227002349550.png" alt="image-20211227001427609" style="zoom:50%;" />

## 下载 ShardingSphere-Proxy

> https://shardingsphere.apache.org/document/current/cn/user-manual/shardingsphere-proxy/startup/bin/
>
> https://www.apache.org/dyn/closer.cgi/shardingsphere/5.0.0/apache-shardingsphere-5.0.0-shardingsphere-proxy-bin.tar.gz

**解压**

```shell
tar zxvf apache-shardingsphere-5.0.0-shardingsphere-proxy-bin.tar.gz
```

### 配置

**server.yaml**

```tex
# governance:
#  name: governance_ds
#  registryCenter:
#    type: ZooKeeper
#    serverLists: localhost:2181
#    props:
#      retryIntervalMilliseconds: 500
#      timeToLiveSeconds: 60
#      maxRetries: 3
#      operationTimeoutMilliseconds: 500
#  overwrite: true

authentication:
 users:
   root:
     password: root
#    sharding:
#      password: sharding 
#      authorizedSchemas: sharding_db

props:
 max-connections-size-per-query: 1
 acceptor-size: 16  # The default value is available processors count * 2.
 executor-size: 16  # Infinite by default.
 proxy-frontend-flush-threshold: 128  # The default value is 128.
   # LOCAL: Proxy will run with LOCAL transaction.
   # XA: Proxy will run with XA transaction.
   # BASE: Proxy will run with B.A.S.E transaction.
 proxy-transaction-type: LOCAL
 proxy-opentracing-enabled: false
 proxy-hint-enabled: false
 query-with-cipher-column: false
 sql-show: true
 check-table-metadata-enabled: false
```

**config-sharding.yaml**

```tex
schemaName: sharding_db

dataSourceCommon:
 username: root
 password: abc123456
 connectionTimeoutMilliseconds: 30000
 idleTimeoutMilliseconds: 60000
 maxLifetimeMilliseconds: 1800000
 maxPoolSize: 5
 minPoolSize: 1
 maintenanceIntervalMilliseconds: 30000

dataSources:
 ds_0:
   url: jdbc:mysql://127.0.0.1:3306/demo_ds_0?serverTimezone=UTC&useSSL=false
 ds_1:
   url: jdbc:mysql://127.0.0.1:3306/demo_ds_1?serverTimezone=UTC&useSSL=false

rules:
- !SHARDING
 tables:
   t_order:
     actualDataNodes: ds_${0..1}.t_order_${0..15}
     tableStrategy:
       standard:
         shardingColumn: order_id
         shardingAlgorithmName: t_order_inline
     keyGenerateStrategy:
       column: order_id
       keyGeneratorName: snowflake

 defaultDatabaseStrategy:
   standard:
     shardingColumn: user_id
     shardingAlgorithmName: database_inline
 defaultTableStrategy:
   none:
 
 shardingAlgorithms:
   database_inline:
     type: INLINE
     props:
       algorithm-expression: ds_${user_id % 2}
   t_order_inline:
     type: INLINE
     props:
       algorithm-expression: t_order_${order_id % 16}
 
 keyGenerators:
   snowflake:
     type: SNOWFLAKE
     props:
       worker-id: 123

```

### 运行

```shell
bin/start.sh
```

但不知咋回事，一直运行不了，搜到是让用解压命令解压，可还是报这个错误，一晚上都没解决，哭了啊。

```tex
Exception in thread "main" Cannot create property=authentication for JavaBean=org.apache.shardingsphere.proxy.config.yaml.YamlProxyServerConfiguration@7d9d1a19
 in 'reader', line 36, column 1:
    authentication:
    ^
Unable to find property 'authentication' on class: org.apache.shardingsphere.proxy.config.yaml.YamlProxyServerConfiguration
 in 'reader', line 37, column 3:
      users:
      ^

	at org.yaml.snakeyaml.constructor.Constructor$ConstructMapping.constructJavaBean2ndStep(Constructor.java:312)
	at org.yaml.snakeyaml.constructor.Constructor$ConstructMapping.construct(Constructor.java:189)
	at org.yaml.snakeyaml.constructor.Constructor$ConstructYamlObject.construct(Constructor.java:345)
	at org.yaml.snakeyaml.constructor.BaseConstructor.constructObject(BaseConstructor.java:182)
	at org.yaml.snakeyaml.constructor.BaseConstructor.constructDocument(BaseConstructor.java:141)
	at org.yaml.snakeyaml.constructor.BaseConstructor.getSingleData(BaseConstructor.java:127)
	at org.yaml.snakeyaml.Yaml.loadFromReader(Yaml.java:450)
	at org.yaml.snakeyaml.Yaml.loadAs(Yaml.java:410)
	at org.apache.shardingsphere.infra.yaml.engine.YamlEngine.unmarshal(YamlEngine.java:56)
	at org.apache.shardingsphere.proxy.config.ProxyConfigurationLoader.loadServerConfiguration(ProxyConfigurationLoader.java:71)
	at org.apache.shardingsphere.proxy.config.ProxyConfigurationLoader.load(ProxyConfigurationLoader.java:58)
	at org.apache.shardingsphere.proxy.Bootstrap.main(Bootstrap.java:46)
Caused by: org.yaml.snakeyaml.error.YAMLException: Unable to find property 'authentication' on class: org.apache.shardingsphere.proxy.config.yaml.YamlProxyServerConfiguration
	at org.yaml.snakeyaml.introspector.PropertyUtils.getProperty(PropertyUtils.java:132)
	at org.yaml.snakeyaml.introspector.PropertyUtils.getProperty(PropertyUtils.java:121)
	at org.yaml.snakeyaml.constructor.Constructor$ConstructMapping.getProperty(Constructor.java:322)
	at org.yaml.snakeyaml.constructor.Constructor$ConstructMapping.constructJavaBean2ndStep(Constructor.java:240)
	... 11 more

```



# 必做题2

**6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。  

