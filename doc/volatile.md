#### volatile关键字
java指导手册是这么介绍volatile关键字：
```text
    The Java programming language allows threads to access shared variables (§17.1). As a rule, to ensure that shared 
variables are consistently and reliably updated, a thread should ensure that it has exclusive use of such variables 
by obtaining a lock that, conventionally, enforces mutual exclusion for those shared variables.
    The Java programming language provides a second mechanism, volatile fields, that is more convenient than locking 
for some purposes.
    A field may be declared volatile, in which case the Java Memory Model ensures that all threads see a consistent 
value for the variable (§17.4).
    java编程语言允许多个线程同时访问共享变量，为了确保共享变量一致且可靠地更新，线程应该使用锁机制来独占使用这些变量，通常是对这些变量
强制使用互斥。
    java提供了第二种机制，即volatile关键字，相比于锁机制而言更加方便。
    一个成员变量使用了volatile关键字声明，java的内存模型会确保多线程看到一致性的一个结果。
```
总结来说，volatile保证了内存可见性，按照java的内存模型，每个线程都有自己单独的内存，当一个线程更新一个位于主内存的共享变量时，该线程会
先将变量从主内存复制到自己的线程内存中，更新完毕后再将结果刷新回主内存，这个几个操作并不是原子性的，volatile总是保证主内存的共享变量值
是最新的，它的做法时：一个volatile变量总是在主内存中，线程对它的读写都是在主内存进行。

在[java的开发手册8.3.1.4](https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.3.1.4)中有一个例子，该例子也可以
本项目的[VolatileTest.java](..%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjia%2Fconcurrent%2FVolatileTest.java)，有两个共享变量i和j，在一个
线程中同时自增，另外一个线程访问这两个变量的结果，偶尔将出现i和j结果不一致的情况，如果对方法加synchronized关键字或者对变量i和j加volatile关
键字，i和j的结果总是一致的。

参考：
- 谷歌搜索"volatile thread safe"
- [java-volatile](https://chercher.tech/java-programming/java-volatile)