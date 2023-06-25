# mysql的日志配置

## 简介
我们这里主要是讨论mysql服务器提供的以便我们监视mysql运行状态的日志，不讨论mysql运行所必须的日志如redolog和undolog这种。
mysql提供了参数选项log_output,允许我们选择将日志写入到磁盘文件或者是mysql的表里，默认值是写到文件。
```text
1。系统log_output变量指定日志输出的目的地。此变量本身并不会启用日志。
2。如果log_output在启动时未指定，则默认日志记录目标为 FILE.
3。如果log_output在启动时指定，它的值是一个列表，一个或多个逗号分隔的单词，
可选值为：自TABLE（记录到表）、 FILE（记录到文件）或 NONE（不记录到表或文件）。NONE，如果存在，优先于任何其他说明符。
4。log_output支持在运行时修改
```

mysql主要为我们提供了以下几种日志：
```text
日志类型                                  说明
Error log	            mysql服务启动，运行和关闭期间产生的问题日志
General query log	    客户端建立连接日志和客户端执行sql语句的日志
Binary log	            数据变更日志（不包括查询）
Relay log	            从节点接受的从主节点发来的数据变更日志
Slow query log	            查询时间大于配置long_query_time制定秒数的查询语句日志
DDL log (metadata log)      ddl语句日志
```

## 查看日志配置
在unix和类unix系统中，mysql8.0的日志开关都是关闭的，要查看已有的日志配置，可以执行命令：
```text
mysql> show variables like '%log%';
```
部分结果如下，如需详细结果，请自行执行该语句：
```text
+------------------------------------------------+---------------------------------------------+
| Variable_name                                  | Value                                       |
+------------------------------------------------+---------------------------------------------+
| general_log                                    | ON                                          |
| general_log_file                               | /var/log/mysqld.log                         |
| log_bin                                        | ON                                          |
| log_bin_basename                               | /var/lib/mysql/binlog                       |
| log_bin_index                                  | /var/lib/mysql/binlog.index                 |
| log_bin_trust_function_creators                | OFF                                         |
| log_bin_use_v1_row_events                      | OFF                                         |
| log_error                                      | stderr                                      |
| log_error_services                             | log_filter_internal; log_sink_internal      |
| log_error_suppression_list                     |                                             |
| log_error_verbosity                            | 2                                           |
| log_output                                     | FILE                                        |
| max_binlog_cache_size                          | 18446744073709547520                        |
| max_binlog_size                                | 1073741824                                  |
| max_binlog_stmt_cache_size                     | 18446744073709547520                        |
| max_relay_log_size                             | 0                                           |
| relay_log                                      | 16a30e5ab2b3-relay-bin                      |
| relay_log_basename                             | /var/lib/mysql/16a30e5ab2b3-relay-bin       |
| relay_log_index                                | /var/lib/mysql/16a30e5ab2b3-relay-bin.index |
| relay_log_info_file                            | relay-log.info                              |
| relay_log_info_repository                      | TABLE                                       |
| relay_log_purge                                | ON                                          |
| relay_log_recovery                             | OFF                                         |
| relay_log_space_limit                          | 0                                           |
| slow_query_log                                 | OFF                                         |
| slow_query_log_file                            | /var/lib/mysql/16a30e5ab2b3-slow.log        |
| sql_log_bin                                    | ON                                          |
| sql_log_off                                    | OFF                                         |
| sync_binlog                                    | 1                                           |
| sync_relay_log                                 | 10000                                       |
| sync_relay_log_info                            | 10000                                       |
| terminology_use_previous                       | NONE                                        |
+------------------------------------------------+---------------------------------------------+
```

## General query log
通用查询日志主要是客户端建立连接和客户端执行sql的日志，主要有以下两个配置
```
# my.cnf
[mysqld]
general_log=1 #1=启用，0=禁用
general_log_file=/opt/logs/mysql_general.log #制定通用日志文件的位置
```
注意如果是log_output制定了将通用查询日志写入到数据表，mysql已经提供了表：mysql.general_log,表结构如下：
```text
#show create table mysql.general_log;
 CREATE TABLE `general_log` (
  `event_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `user_host` mediumtext NOT NULL,
  `thread_id` bigint unsigned NOT NULL,
  `server_id` int unsigned NOT NULL,
  `command_type` varchar(64) NOT NULL,
  `argument` mediumblob NOT NULL
) ENGINE=CSV DEFAULT CHARSET=utf8mb3 COMMENT='General log'
```
还支持在运行期间实时更改，如下命令：
```text
 set [global] general_log=ON;
 set [global] general_log_file=/opt/logs/mysql_general.log;
```

## Slow query log
慢查询日志主要是查询时间大于配置long_query_time制定秒数的查询语句日志
```
# my.cnf
[mysqld]
slow_query_log=1
slow_query_log_file=/opt/logs/mysql_slow_query.log
long_query_time=10 #设置超过10秒sql为慢查询
```
注意如果是log_output制定了将慢查询日志写入到数据表，mysql已经提供了表：mysql.slow_log,表结构如下：
```text
#show create table mysql.slow_log;
 CREATE TABLE `slow_log` (
  `start_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `user_host` mediumtext NOT NULL,
  `query_time` time(6) NOT NULL,
  `lock_time` time(6) NOT NULL,
  `rows_sent` int NOT NULL,
  `rows_examined` int NOT NULL,
  `db` varchar(512) NOT NULL,
  `last_insert_id` int NOT NULL,
  `insert_id` int NOT NULL,
  `server_id` int unsigned NOT NULL,
  `sql_text` mediumblob NOT NULL,
  `thread_id` bigint unsigned NOT NULL
) ENGINE=CSV DEFAULT CHARSET=utf8mb3 COMMENT='Slow log';
```

类似的，还支持在运行期间实时更改，如下命令：
```text
set [global] slow_query_log=ON;
set [global] slow_query_log_file=/opt/logs/mysql_slow_query.log;
set [global] long_query_time=10;
```

## The Error Log
mysql的错误日志记录了mysql启动，运行和关闭期间的日志，这里的日志不仅仅是错误日志，还可以是提醒级别的日志，mysql提供了丰富的配置供你使用。
在mysql8.0中，mysql的错误日志使用了组件化的实现方式，你可以很方便的定义日志输出的内容，日志级别和日志输出目的地等等。更方便的是，mysql
可以接受用户自定义的日志组件。
在linux系统中，如果使用yum方式按照，默认的错误日志路径是/var/log/mysql.log。
```text
#my.cnf
[mysqld]
log_error=/var/log/mysql/mysql.err  #默认值是stderr，即输出到控制台
log_error_services='log_filter_internal; log_sink_internal; log_sink_json'
```

## The Binary log
binlog日志也叫二进制日志，有两个作用：主从复制传输作为数据的载体和数据恢复，基于后者，binlog日志总是建议打开的。
MySQL 8.0 开始，二进制日志记录默认启用，只有在启动时指定 --skip-log-bin 或 --disable-log-bin 选项时才会禁用，并且默认是写到mysql的
数据目录下。
```text
#my.cnf
[mysqld]
log_bin=mybinlog  
```
log_bin不是一个开关配置，它是指定binlog日志的基本名字(不含后缀，默认"binlog")，例如根据上图的配置，实际的binlog日志是mybinlog.000001,
mybinlog.000002,后面的序号递增。
在mysql每一次启动中，mysql都会创建新的binlog日志。


## 参考资料
[mysql8-5.4 MySQL Server Logs](https://dev.mysql.com/doc/refman/8.0/en/server-logs.html)
