# 可重入读写锁ReentrantReadWriteLock的原理[👉processon](https://www.processon.com/view/link/65df0bee3145661f1489e30e)

## 特性或者原理
### 1.state的含义
由于state是int类型，在java中占4个字节，32个bit位，低16位（右边16位）代表写锁的状态，准确的来说是当前持有写锁资源的线程的重入次数，高16位（左边16位）
代表的加读锁的次数（重入次数也计算在内），它是通过位运算<<<（零填充右移）来实现的，至于每个读线程重入的次数维护在每个线程的线程本地变量ThreadLocal中。

### 2.可重入含义解释
可重入是针对同一个线程同一种锁而言的：
- 读-读可重入：
```text
线程a已经加过一次读锁了，当它再次加读锁时，依然可以获取锁资源成功。首个加读锁线程会累加firstReaderHoldCount表示重入次数，非首个加读锁线程会在
ThreadLocal中维护一个计数器count表示重入次数。
```
- 写-写可重入：
```text
线程a先前已经加过写锁了，此时AQS.exclusiveOwnerThread=线程a，AQS.state=1，线程a再次加读锁时，将AQS.state加1后返回，表示获取锁资源成功。
```

### 3.锁的升降级
- 写锁可降级成读锁：
```text
如果此前线程a已经加过写锁了，此时AQS.exclusiveOwnerThread=线程a，
```
- 读锁不可升级成写锁
```text
线程a已经加过一次读锁了，线程a接下来尝试获取写锁时，线程a将会park住自己而造成死锁，这是因为线程a的前节点为空没有线程会去唤醒它
```