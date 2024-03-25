# java的日志体系

## 发展历程

1.jdk1.3之前的使用System.out打印日志或者输出日志到文件。

2.瑞士程序大牛Ceki Gülcü开发了log4j，包括了日志接口和日志实现，后来该项目捐献给Apache。

3.apache想将log4j并入到jdk中遭到拒绝，sun公司在jdk1.4开发了自己的日志，位于java.util.logging下，所以一般简称为jul。

4.apache开发了一套日志规范，也就是日志接口并没有具体的实现，叫Jakarta Commons Logging，简称jcl。spring框架使用了jcl。

5.上面的程序大牛开发了自己的日志规范slf4j和日志实现logback，logback有更高的性能。

6.Apache参照logback，升级了log4j，也就是log4j2。

## log4j


