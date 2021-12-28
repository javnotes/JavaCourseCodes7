# ShardingSphere-Proxy 水平分库分表

> 演示水平分库分表，拆分 2 个库，每个库 16 张表。
>
> 注意：此次演示中的数据库是在同一MySQL实例中的。

## 建库

```sql
show schemas;
create schema demo_ds_0;
create schema demo_ds_1;
show schemas;
```

<img src="https://s2.loli.net/2021/12/28/zxGwFkXvMcyNmpn.png" alt="image-20211227001243424" style="zoom:50%;" />

## 下载 ShardingSphere-Proxy 5.0.0-alpha

> https://www.apache.org/dyn/closer.cgi/shardingsphere/5.0.0-alpha/apache-shardingsphere-5.0.0-alpha-shardingsphere-proxy-bin.tar.gz
>
> 注意是 alpha 版本。其实老师一开始就说了，白忙活了三天...

**解压**

```shell
tar zxvf apache-shardingsphere-5.0.0-alpha-shardingsphere-proxy-bin.tar.gz
```

**引入依赖**

下载 ``mysql-connector-java-8.0.11.jar``，并将其放入 ``ext-lib`` 目录。

> https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.11/mysql-connector-java-8.0.11.jar

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

打开 ``start.bat``

> Mac 执行命令 bin/start.sh

![image-20211228144853519](https://s2.loli.net/2021/12/28/wR3XtEkKuCabp24.png)

可以观察到在窗口提示信息，启动成功。如果未成功，先检查下数据库的连接配置信息。

```sql
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

<img src="https://s2.loli.net/2021/12/28/uCnKezyTw4sl7AS.png" style="zoom:50%;" />

输入建表语句，让  ``ShardingSphere-Proxy`` 根据先前配置好的规则，自动建表。

```sql
show schemas;
use sharding_db;
CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL AUTO_INCREMENT, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id));
```

![image-20211228151112039](https://s2.loli.net/2021/12/28/fHvDTSObu4kwcod.png)

观察日志，可以得到实际执行的SQL语句。这样，  ``ShardingSphere-Proxy`` 自动在每一个库中，创建了16张表。

```sql
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

## 增删改查

### INSERT

```sql
INSERT t_order(user_id, status) VALUES(1, 'ok');
INSERT t_order(user_id, status) VALUES(1, 'ok');(2, 'ok'), (3, 'ok'),(4, 'ok');
```

**日志**

```sql
[INFO ] 15:59:08.040 [ShardingSphere-Command-15] ShardingSphere-SQL - Actual SQL: ds_1 ::: INSERT t_order_0(user_id, status, order_id) VALUES(1, 'ok', 682617724517986304)
[INFO ] 16:02:00.790 [ShardingSphere-Command-5] ShardingSphere-SQL - Logic SQL: INSERT t_order(user_id, status) VALUES(1, 'ok'),(2, 'ok'), (3, 'ok'),(4, 'ok')
[INFO ] 16:02:00.791 [ShardingSphere-Command-5] ShardingSphere-SQL - SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
[INFO ] 16:02:00.793 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: INSERT t_order_0(user_id, status, order_id) VALUES(1, 'ok', 682618449461489664)
[INFO ] 16:02:00.793 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: INSERT t_order_1(user_id, status, order_id) VALUES(2, 'ok', 682618449461489665)
[INFO ] 16:02:00.794 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_1 ::: INSERT t_order_2(user_id, status, order_id) VALUES(3, 'ok', 682618449461489666)
[INFO ] 16:02:00.795 [ShardingSphere-Command-5] ShardingSphere-SQL - Actual SQL: ds_0 ::: INSERT t_order_3(user_id, status, order_id) VALUES(4, 'ok', 682618449461489667)
```

### SELECT

```sql
select * from t_order;

mysql> select * from t_order;
+--------------------+---------+--------+
| order_id           | user_id | status |
+--------------------+---------+--------+
| 682618449461489665 |       2 | ok     |
| 682618449461489667 |       4 | ok     |
| 682617724517986304 |       1 | ok     |
| 682618449461489664 |       1 | ok     |
| 682618390334386177 |       1 | ok     |
| 682618449461489666 |       3 | ok     |
+--------------------+---------+--------+
6 rows in set (0.08 sec)
```

**日志**

可以看出，如果条件不限定，实际的SQL是需要查询每一张表的。

```sql
[INFO ] 16:02:04.610 [ShardingSphere-Command-6] ShardingSphere-SQL - Logic SQL: select * from t_order
[INFO ] 16:02:04.610 [ShardingSphere-Command-6] ShardingSphere-SQL - SQLStatement: MySQLSelectStatement(limit=Optional.empty, lock=Optional.empty)
[INFO ] 16:02:04.613 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_0
[INFO ] 16:02:04.613 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_1
[INFO ] 16:02:04.613 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_2
[INFO ] 16:02:04.615 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_3
[INFO ] 16:02:04.615 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_4
[INFO ] 16:02:04.616 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_5
[INFO ] 16:02:04.616 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_6
[INFO ] 16:02:04.618 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_7
[INFO ] 16:02:04.618 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_8
[INFO ] 16:02:04.619 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_9
[INFO ] 16:02:04.619 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_10
[INFO ] 16:02:04.619 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_11
[INFO ] 16:02:04.620 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_12
[INFO ] 16:02:04.620 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_13
[INFO ] 16:02:04.621 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_14
[INFO ] 16:02:04.628 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_15
[INFO ] 16:02:04.628 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_0
[INFO ] 16:02:04.629 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1
[INFO ] 16:02:04.629 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_2
[INFO ] 16:02:04.630 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_3
[INFO ] 16:02:04.631 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_4
[INFO ] 16:02:04.631 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_5
[INFO ] 16:02:04.632 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_6
[INFO ] 16:02:04.634 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_7
[INFO ] 16:02:04.634 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_8
[INFO ] 16:02:04.640 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_9
[INFO ] 16:02:04.643 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_10
[INFO ] 16:02:04.645 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_11
[INFO ] 16:02:04.646 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_12
[INFO ] 16:02:04.646 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_13
[INFO ] 16:02:04.647 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_14
[INFO ] 16:02:04.647 [ShardingSphere-Command-6] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_15
```

如果条件限定，就可以仅查询特定的（库）表

```sql
select * from t_order where order_id = 682618449461489665 and user_id = 1;
```

```sql
[INFO ] 16:07:45.559 [ShardingSphere-Command-8] ShardingSphere-SQL - Logic SQL: select * from t_order where order_id = 682618449461489665 and user_id = 1
[INFO ] 16:07:45.559 [ShardingSphere-Command-8] ShardingSphere-SQL - SQLStatement: MySQLSelectStatement(limit=Optional.empty, lock=Optional.empty)
[INFO ] 16:07:45.560 [ShardingSphere-Command-8] ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1 where order_id = 682618449461489665 and user_id = 1
```

## UPDATE

```sql
update t_order set status = 'fail' where order_id = 682618449461489665 and user_id = 1;
```

**日志**

```sql
[INFO ] 16:51:03.974 [ShardingSphere-Command-11] ShardingSphere-SQL - Logic SQL: update t_order set status = 'fail' where order_id = 682618449461489665 and user_id = 1
[INFO ] 16:51:03.974 [ShardingSphere-Command-11] ShardingSphere-SQL - SQLStatement: MySQLUpdateStatement(orderBy=Optional.empty, limit=Optional.empty)
[INFO ] 16:51:03.975 [ShardingSphere-Command-11] ShardingSphere-SQL - Actual SQL: ds_1 ::: update t_order_1 set status = 'fail' where order_id = 682618449461489665 and user_id = 1
```

## DELETE

```sql
delete from t_order where order_id = 682618449461489665;
```

**日志**

```sql
[INFO ] 17:00:56.018 [ShardingSphere-Command-12] ShardingSphere-SQL - Logic SQL: delete from t_order where order_id = 682618449461489665
[INFO ] 17:00:56.019 [ShardingSphere-Command-12] ShardingSphere-SQL - SQLStatement: MySQLDeleteStatement(orderBy=Optional.empty, limit=Optional.empty)
[INFO ] 17:00:56.020 [ShardingSphere-Command-12] ShardingSphere-SQL - Actual SQL: ds_0 ::: delete from t_order_1 where order_id = 682618449461489665
[INFO ] 17:00:56.020 [ShardingSphere-Command-12] ShardingSphere-SQL - Actual SQL: ds_1 ::: delete from t_order_1 where order_id = 682618449461489665
```

## 报错

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

