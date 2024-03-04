# 可重入锁ReentrantLock[👉processon](https://www.processon.com/view/link/65d702a69efaa83afcc88700)
## 特性或者原理
### 1.可重入的实现原理
```text
在加锁的代码中，ReentrantLock实现的了AQS的tryAcquire方法，加锁成功了会将AQS.exclusiveOwnerThread属性设置为获取锁资源成功的线程，当此线程再次
加锁时，tryAcquire方法会判断当前加锁的线程是否等于exclusiveOwnerThread，会将state加1，然后直接返回，代表获取锁资源成功。
```
### 2.公平和非公平的实现原理
```text
所谓的非公平，指的的是外面来的线程可以直接和等待队列中的头结点线程竞争锁资源。公平锁的话限制了外面线程的这种权利，在获取锁资源前，会判断AQS的等待队列
是否有等待的线程，如果有会将外面来的线程加入到等待队列的尾部等待。
```