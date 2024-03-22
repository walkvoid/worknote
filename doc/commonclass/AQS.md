# AQS（AbstractQueuedSynchronizer）
## 特性
是一个实现阻塞式锁和依赖FIFO队列实现线程同步的框架,里面维护了一个int类型的值state,子类必须自己实现方法更改state的值,以及定义state是哪些值
时可以获取和释放.

提供了独占模式和共享模式,独占模式模式对应了tryAcquire方法和tryRelease方法,共享模式对应了tryAcquireShared方法和tryReleaseShared方法,
在独占模式,当一个线程获取锁资源成功,其他线程将无法获取此资源.共享模式允许多个线程获取同一个锁资源.

共享模式最


参考：
- [从源码层面解析yield、sleep、wait、park](https://juejin.cn/post/6844903971463626766)