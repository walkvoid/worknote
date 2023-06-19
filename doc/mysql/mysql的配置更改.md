# mysql的配置更改
## 一. 简介
mysql服务有很多运行的参数，可以在mysql服务启动时使用启动附加参数和配置文件进行参数的制定。mysql也支持在运行时更改配置参数，这时主要是通过命令行
的方式，更改运行时的参数又分为更改全局参数和更改当前连接（session）的参数。如果要做一个层次区分，它开起来是这样的：
```
启动时
    |--启动参数
    |--配置文件
运行时
    |--命令行
        |--全局配置
        |--当前连接配置        
```
mysql的配置浩如烟海，我们这里并不是要介绍mysql具体的配置，而是介绍如何更改mysql的配置参数。
## 二.查看mysql的配置
当mysql服务在正常运行的状态，我们可以使用命令语句查看mysql的一些配置。
- todo

## 三.mysql配置文件加载位置-unix
在unix系统或者类unix系统中，例如centos或者macOS系统，mysql的配置文件默认以.cnf作为文件后缀
按下图的顺序加载，如果有相同的配置，后面加载的配置项会覆盖前面的：
```text
  File Name	                   Purpose
/etc/my.cnf	                Global options
/etc/mysql/my.cnf	        Global options
SYSCONFDIR/my.cnf	        Global options
$MYSQL_HOME/my.cnf	        Server-specific options (server only)
defaults-extra-file             The file specified with --defaults-extra-file, if any
~/.my.cnf	                User-specific options
~/.mylogin.cnf	                User-specific login path options (clients only)
DATADIR/mysqld-auto.cnf	        System variables persisted with SET PERSIST or SET PERSIST_ONLY (server only)
```

一份典型的配置文件my.cnf看起来是这样的：
```text
[client]
port=3306
socket=/tmp/mysql.sock

[mysqld]
port=3306
socket=/tmp/mysql.sock
key_buffer_size=16M
max_allowed_packet=128M

[mysqldump]
quick

!includedir /etc/mysql/conf.d/
```
在上面的配置文件中，[client]，[mysqld]和[mysqldump]表示不同的分组，例如[client]下面的配置表示mysql客户端的配置，[mysqld]下面的配置表示
服务端的配置。

在最下面的一行!includedir /etc/mysql/conf.d/ 表示mysql也将加载/etc/mysql/conf.d/下的配置文件，但是配置文件必须是 .cnf结尾，需要
注意的是，用这种方式加载的配置文件加载顺序是随机的。
## 参考资料
[mysql8.0-4.2.2.2 Using Option Files](https://dev.mysql.com/doc/refman/8.0/en/option-files.html)
