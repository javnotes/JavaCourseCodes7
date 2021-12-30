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

## 建库

```sql
show schemas;
create schema demo_ds_0;
create schema demo_ds_1;
show schemas;
```

<img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211227001243424-20211227002349522.png" alt="image-20211227001243424" style="zoom:50%;" />

## 下载 ShardingSphere-Proxy 5.0.0-alpha

> 注意是 alpha 版本。其实老师一开始就说了，白忙活了三天...

**解压**

```shell
tar zxvf apache-shardingsphere-5.0.0-alpha-shardingsphere-proxy-bin.tar.gz
```

**引入依赖**

下载 ``mysql-connector-java-8.0.11.jar``，并将其放入 ``ext-lib`` 目录。

### 配置

**server.yaml**

```tex
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

######################################################################################################
# 
# If you want to configure governance, authorization and proxy properties, please refer to this file.
# 
######################################################################################################
#
#governance:
#  name: governance_ds
#  registryCenter:
#    type: ZooKeeper
#    serverLists: localhost:2181
#    props:
#      retryIntervalMilliseconds: 500
#      timeToLiveSeconds: 60
#      maxRetries: 3
#      operationTimeoutMilliseconds: 500
#  overwrite: false

authentication:
  users:
    root:
      password: root
    sharding:
      password: sharding
      authorizedSchemas: sharding_db

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
  query-with-cipher-column: true
  sql-show: true
  check-table-metadata-enabled: false
```

**config-sharding.yaml**

```tex
######################################################################################################
#
# If you want to connect to MySQL, you should manually copy MySQL driver to lib directory.
#
######################################################################################################

schemaName: sharding_db

dataSourceCommon:
  username: root
  password: abc123456
  connectionTimeoutMilliseconds: 30000
  idleTimeoutMilliseconds: 60000
  maxLifetimeMilliseconds: 1800000
  maxPoolSize: 50
  minPoolSize: 1
  maintenanceIntervalMilliseconds: 30000

dataSources:
  ds_0:
    url: jdbc:mysql://127.0.0.1:3306/demo_ds_0?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
  ds_1:
    url: jdbc:mysql://127.0.0.1:3306/demo_ds_1?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true

rules:
- !SHARDING
  tables:
    t_order:
      actualDataNodes: ds_${0..1}.t_order_${0..15}
      tableStrategy:
        standard:
          shardingColumn: order_id
          shardingAlgorithmName: t_order_inline
#      keyGenerateStrategy:
#        column: order_id
#        keyGeneratorName: snowflake
#    t_order_item:
#      actualDataNodes: ds_${0..1}.t_order_item_${0..1}
#      tableStrategy:
#        standard:
#          shardingColumn: order_id
#          shardingAlgorithmName: t_order_item_inline
#      keyGenerateStrategy:
#        column: order_item_id
#        keyGeneratorName: snowflake
#  bindingTables:
#    - t_order,t_order_item
  defaultDatabaseStrategy:
    standard:
      shardingColumn: user_id
      shardingAlgorithmName: database_inline
#  defaultTableStrategy:
#    none:
#  
  shardingAlgorithms:
    database_inline:
      type: INLINE
      props:
        algorithm-expression: ds_${user_id % 2}
    t_order_inline:
      type: INLINE
      props:
        algorithm-expression: t_order_${order_id % 16}
#    t_order_item_inline:
#      type: INLINE
#      props:
#        algorithm-expression: t_order_item_${order_id % 2}
#  
#  keyGenerators:
#    snowflake:
#      type: SNOWFLAKE
#      props:
#        worker-id: 123
```

### 运行

打开 ``start.bat``

![image-20211228144853519](https://s2.loli.net/2021/12/28/jen9HQFC5I618vP.png)

可以观察到在窗口提示信息，启动成功。如果未成功，先检查下数据库的连接配置信息。

```shell
Starting the ShardingSphere-Proxy ...
[INFO ] 10:30:23.807 [main] ShardingSphere-metadata - Loading 0 tables' meta data for unconfigured tables.
[INFO ] 10:30:23.813 [main] ShardingSphere-metadata - Loading 0 tables' meta data for unconfigured tables.
[INFO ] 10:30:23.820 [main] ShardingSphere-metadata - Loading 0 tables' meta data for unconfigured tables.
[INFO ] 10:30:23.822 [main] ShardingSphere-metadata - Loading 0 tables' meta data for unconfigured tables.
[INFO ] 10:30:23.836 [main] o.a.s.i.c.s.SchemaContextsBuilder - Load meta data for schema sharding_db finished, cost 111 milliseconds.
Thanks for using Atomikos! Evaluate http://www.atomikos.com/Main/ExtremeTransactions for advanced features and professional support
or register at http://www.atomikos.com/Main/RegisterYourDownload to disable this message and receive FREE tips & advice
[INFO ] 10:30:23.940 [main] o.a.s.p.i.i.AbstractBootstrapInitializer - Database name is `MySQL`, version is `8.0.26`
[INFO ] 10:30:24.938 [main] o.a.s.p.frontend.ShardingSphereProxy - ShardingSphere-Proxy start success.
```

## 建表

登录  ``ShardingSphere-Proxy`` ，注意：端口是 3307

```sql
mysql -h 127.0.0.1 -P 3307 -uroot -p
```

<img src="https://s2.loli.net/2021/12/28/FUX8nPH5eiGtw2Q.png" style="zoom:50%;" />

输入建表语句，让  ``ShardingSphere-Proxy`` 根据先前配置好的规则，自动建表。

```sql
show schemas;
use sharding_db;
CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
```

![image-20211228151112039](https://s2.loli.net/2021/12/28/RJ7FMZ8nuk2Q6fS.png)

观察日志，可以得到实际执行的SQL语句。这样，  ``ShardingSphere-Proxy`` 自动在每一个库中，创建了16张表。

```shell
[INFO ] 10:56:12.584 [ShardingSphere-Command-5] ShardingSphere-SQL - Logic SQL: CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.585 [ShardingSphere-Command-5] ShardingSphere-SQL - SQLStatement: MySQLCreateTableStatement(isNotExisted=true)
[INFO ] 10:56:12.586 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_0 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.586 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_1 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.586 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_2 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.587 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_3 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.587 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_4 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.587 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_5 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.588 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_6 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.588 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_7 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.588 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_8 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.589 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_9 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.589 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_10 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.590 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_11 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.591 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_12 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.592 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_13 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.592 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_14 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.593 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: CREATE TABLE IF NOT EXISTS t_order_15 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.593 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_0 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.594 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_1 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.595 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_2 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.600 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_3 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.601 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_4 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.601 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_5 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.602 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_6 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.603 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_7 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.603 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_8 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.604 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_9 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.604 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_10 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.605 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_11 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.605 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_12 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.606 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_13 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.606 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_14 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:12.607 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: CREATE TABLE IF NOT EXISTS t_order_15 (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))
[INFO ] 10:56:21.961 [ShardingSphere-Command-6] ShardingSphere-SQL - Logic SQL: show tables
[INFO ] 10:56:21.961 [ShardingSphere-Command-6] ShardingSphere-SQL - SQLStatement: MySQLShowTablesStatement(fromSchema=Optional.empty)
[INFO ] 10:56:21.963 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: show tables
```

## 报错

### CMS

查看启动脚本得知，Sharding-Proxy 指定 GC 为CMS，``-XX:+UseConcMarkSweepGC``，而  CMS GC 在 JDK14 中已被删除

**脚本**

```shell
java -server -Xmx2g -Xms2g -Xmn1g -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Dfile.encoding=UTF-8 -classpath %CLASS_PATH% %MAIN_CLASS%
```

**参靠链接**

> https://www.javacodegeeks.com/2019/11/jdk-14-cms-gc-is-obe.html

### Cannot create property=authentication for JavaBean=org.apache.shardingsphere.proxy.config.yaml.YamlProxyServerConfiguration@7d9d1a19 in 'reader'

如果出现下面的错误，是版本用错了。要使用 alpha。

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

> **6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。  

## 环境准备

启动两个 MySQL Server

```sql
# 登录信息如下
mysql -h127.0.0.1 -P 3336 -uroot
mysql -h127.0.0.1 -P 3346 -uroot
# 建库建表
create database demo_ds;
use demo_ds;
CREATE TABLE IF NOT EXISTS t_order_0 (order_id BIGINT NOT NULL, user_id INT NOT NULL, PRIMARY KEY (order_id));
CREATE TABLE IF NOT EXISTS t_order_1 (order_id BIGINT NOT NULL, user_id INT NOT NULL, PRIMARY KEY (order_id));
```

## 代码







## 参考链接

> [分布式事务](https://shardingsphere.apache.org/document/legacy/4.x/document/cn/manual/sharding-jdbc/usage/transaction/)
>
> [ShardingSphere RAW JDBC 分布式事务XA 代码示例](https://blog.csdn.net/github_35735591/article/details/110734467)
