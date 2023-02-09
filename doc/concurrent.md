##### java并发编程知识树
- 我们先从java的volatile关键字开始，要理解volatile关键字，则有必要关注一下java的内存模型，
还可以学习volatile的经典应用双重检查锁的实现细节。
- 还有一个synchronized关键字，理解了synchronized，我们需要关注一下java锁升级的知识，然后
我们可以对比一下Lock锁，关注Lock的接口方法，我们可以学习一下线程中断这个概念，深入理解
Lock锁的的两个实现ReentrantLock和ReentrantReadWriteLock，要理解这两个实现，
就不得不先学习AQS，在学习AQS过程中，我们需要学习Thread和Unsafe这两个类，学习Thread中，我们自然而然会
接触到ThreadLocal，我们就可以关注另一块集合框架的知识了。
总结一下上面的文字：
```
volatile关键字 
    |- java内存模型
    |- 双重检查锁
    
synchronized关键字
    |- java锁升级
    |- Lock接口
        ｜- 线程中断
        ｜- ReentrantLock和ReentrantReadWriteLock
            ｜- AQS
                ｜- Unsafe
                ｜- Thread
                    ｜- ThreadLocal
```
所有链接：
[volatile关键字](./volatile.md)

