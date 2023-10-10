
```sql
CREATE TABLE `seq_no` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `prefix` varchar(32) NOT NULL DEFAULT '' COMMENT '前缀(区分不同的业务)',
  `counter` bigint(20) DEFAULT '1' COMMENT '计数器',
  `counter_date` date DEFAULT NULL COMMENT '计数日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务编号表';
```
```sql
mysql> select * from seq_no;
+----+--------+---------+--------------+
| id | prefix | counter | counter_date |
+----+--------+---------+--------------+
|  1 | ORDER  |       0 | 2023-09-05   |
|  2 | ORDER  |       0 | 2023-09-06   |
|  3 | ORDER  |       0 | 2023-09-07   |
|  4 | ORDER  |       0 | 2023-09-08   |
|  5 | ORDER  |       0 | 2023-09-09   |
+----+--------+---------+--------------+
```

```text
incr_sql = update seq_no set counter=counter+1 where prefix='ORDER' and counter_date='2020-09-05';
     
 1    session1                       session2
         |                              |
 2     start                          start
         |                              |
 3    incr_sql                       incr_sql
         |                              |
 4       |                           blocking
         |                              |
 5     commit                           |   
                                        |
 6                                    commit   
```
