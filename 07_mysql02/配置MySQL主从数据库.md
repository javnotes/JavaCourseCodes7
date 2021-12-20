# 编写数据库实例配置文件

本机上要先安装数据库。比如我是通过HomeBrew安装了MySQL8。

## 主库

- 新建文件夹，`mysql1` 用于主库，`mysql2` 用于从库

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220210424284.png" alt="image-20211220210424284" style="zoom:50%;" />

- 进入文件夹 `mysql1`，创建主库的配置文件 `my.cnf`

  ```tex
  # Default Homebrew MYSQL server config
  [mysqld]
  # Only allow connections from Localhost
  bind-address=127.0.0.1
  port=3316
  server-id=1
  datadir=/Users/luf/data/mysql/mysql1/data
  socket=/tmp/mysql3316.sock
  
  sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
  log_bin=mysql-bin
  binlog-format=Row
  ```

## 从库

- 进入文件夹 `mysql2`，创建从库的配置文件 `my.cnf`

  ```
  # Default Homebrew MYSQL server config
  [mysqld]
  # Only allow connections from Localhost
  bind-address=127.0.0.1
  port=3326
  server-id=2
  datadir=/Users/luf/data/mysql/mysql2/data
  socket=/tmp/mysql3326.sock
  
  #basedir = ./
  #datadir = ./data
  #port = 3306
  #server_id = 1
  
  sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
  log_bin=mysql-bin
  binlog-format=Row
  ```

# 数据库实例的初始化

## 主库

- 在路径  `mysql1` 下，在终端中执行

  ```shell
  mysqld --defaults-file=my.cnf --initialize-insecure
  ```

- 查看初始化是否成功，有文件夹  `data` 即初始化成功

  ```shell
  ll
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220212811482.png" alt="image-20211220212811482" style="zoom:50%;" />

- 启动主库实例，在终端中输入

  ```shell
  mysqld --defaults-file=my.cnf
  ```

- 登录主库

  ```shell
  mysql -h127.0.0.1 -P 3316 -uroot
  ```

- 查看当前数据库的端口，确认为主库

  ```sql
  show variables like '%port%';
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220213753608.png" alt="image-20211220213753608" style="zoom:50%;" />

## 从库

- 在路径  `mysql2` 下，在终端中执行

  ```shell
  mysqld --defaults-file=my.cnf --initialize-insecure
  ```

- 查看初始化是否成功，有文件夹  `data` 即初始化成功

  ```shell
  ll
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220213917514.png" alt="image-20211220213917514" style="zoom:50%;" />

- 启动主库实例，在终端中输入

  ```shell
  mysqld --defaults-file=my.cnf
  ```

- 登录主库

  ```shell
  mysql -h127.0.0.1 -P 3326 -uroot
  ```

- 查看当前数据库的端口，确认为从库

  ```sql
  show variables like '%port%';
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220214005569.png" alt="image-20211220214005569" style="zoom:50%;" />

# 配置节点

## 配置主节点（mysql1）

- 登录主库

  ```shell
  mysql -h127.0.0.1 -P 3316 -uroot
  ```

- 新增用户，专门用于复制

  ```sql
  CREATE USER 'repl'@'%' IDENTIFIED BY 'abc123456';
  ```

- 授权用户复制的权限

  ```sql
  GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
  ```

- 刷新权限表

  ```sql
  flush privileges;
  ```

- 查看当前库（主库）的状态

  ```sql
  show master status;
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220221451941.png" alt="image-20211220221451941" style="zoom:50%;" />

## 配置从节点（mysql2）

- 登录从库

  ```shell
  mysql -h127.0.0.1 -P 3326 -uroot
  ```

- 执行 SQL 语句，注意下面语句要更改为你的配置

  ```sql
  CHANGE MASTER TO
      MASTER_HOST='127.0.0.1',
      MASTER_PORT = 3316,
      MASTER_USER='repl',
      MASTER_PASSWORD='abc123456',
      MASTER_LOG_FILE='mysql-bin.000002',
      MASTER_LOG_POS=857;
  ```

> 注意，当SQL命令执行有警告，则可查看警告
>
> ```sql
> show warnings;
> ```
>
> 查看从数据库状态
>
> ```sql
> show slave status \G
> ```

# 开启主从复制

- 在从数据库客户端中，执行

  ```sql
  start slave;
  ```

- 查看从数据库状态

  ```sql
  show slave status \G
  ```

  ![image-20211220225724491](https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220225724491.png)

# 测试

- 在主库中执行

  ```sql
  show schemas;
  create schema db;
  use db;
  create table t1(id int);
  insert into t1(id) values(1),(2);
  select id from t1;
  ```

- 在从库中执行

  ```sql
  use db;use 
  select id from t1;
  ```

  <img src="https://vuffy.oss-cn-shenzhen.aliyuncs.com/img/image-20211220230420525.png" alt="image-20211220230420525" style="zoom:50%;" />

  因为建库、建表、插值语句是在主库中执行的，而在从库中也存在相同的库、表、数据，就证明了主从配置成功。

# 暂停同步

因为配置方式是 bin_log 方式，所以在主库中，暂时关闭bin_log

```sql
# 关闭bin_log
set SQL_LOG_BIN=0;
# 开启bin_log
set SQL_LOG_BIN=1;
```

# 其他

> GTID与复制：
>
> https://blog.51cto.com/13540167/2086045
>
> https://www.cnblogs.com/zping/p/10789151.html
>
> 半同步复制：
>
> https://www.cnblogs.com/zero-gg/p/9057092.html
>
> 组复制：
>
> https://www.cnblogs.com/lvxqxin/p/9407080.html  

