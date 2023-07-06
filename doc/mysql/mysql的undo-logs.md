# Undo Logs
## 简介
严格来说，undo logs是属于innodb引擎的，mysql的其他引擎例如MyISAM就没有undo logs，这很容易解释，因为undo logs是为了实现事务的，而
MyISAM不具备事务特性。
一个读写事务通常会产生很多个undo log，而一个undo log记录的是如何撤销最近一次更改的信息。
如果事务A在一致性读的隔离条件下，需要看到某条数据的原始状态（此条数据已被另一个事务B修改，但是事务B还没有提交），此时事务A需要的数据将从
undo log中检索。
undo log存在于undo log segments中，undo log segments又存在rollback segments中，而rollback segments 又存在undo tablespaces（
存放常规的表的undo log）和 global temporary tablespaces（存放临时表undo log）中。注意，临时表是没有redo log（另一种日志）的，因为它
不需要崩溃恢复，临时表的undo log仅仅是用在mysql运行时用作回滚。

## 配置
每个undo tablespace最大支持128个rollback segment，global temporary tablespace 也是一样的。
```
mysql> show variables like '%rollback%';
+----------------------------+-------+
| Variable_name              | Value |
+----------------------------+-------+
| innodb_rollback_on_timeout | OFF   |
| innodb_rollback_segments   | 128   |
+----------------------------+-------+

mysql> show variables like '%undo_tablespaces%';
+-------------------------+-------+
| Variable_name           | Value |
+-------------------------+-------+
| innodb_undo_tablespaces | 2     |
+-------------------------+-------+
```
因为undo tablespace有两个，所以默认的rollback segment一共有256个（128*2）。默认的这两个表空间对应了data目录下的文件：
undo_001和undo_002

## 估算musql事务并发数
针对常规表，insert操作是一种undo log， update和delete视为同一种undo log，因为在mysql中，删除仅仅是更新记录的标记为删除状态。
undo log槽（undo log slot）：等于innodb页大小（单位字节）/16，所以对于16k的页，undo log槽数量等于1024（16*1024）/16。
常规表对于单insert的事务，支持的最大并发数为：undo log槽数量 * rollback segment数量 * undo tablespace数量。
所以并发数为：1024*128*2=262144。如果是一个事务insert和update（delete）操作都有，需要用前面的公式除以2，也就是131072。


# 参考
[mysql8-15.6.6 Undo Logs](https://dev.mysql.com/doc/refman/8.0/en/innodb-undo-logs.html)
[mysql8-15.6.3.4 Undo Tablespaces](https://dev.mysql.com/doc/refman/8.0/en/innodb-undo-tablespaces.html)
