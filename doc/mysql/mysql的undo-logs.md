# Undo Logs
## 简介
严格来说，undo logs是属于innodb引擎的，mysql的其他引擎例如MyISAM就没有undo logs，这很容易解释，因为undo logs是为了实现事务的，而
MyISAM不具备事务特性。


# 参考
[mysql8-15.6.6 Undo Logs](https://dev.mysql.com/doc/refman/8.0/en/innodb-undo-logs.html)
