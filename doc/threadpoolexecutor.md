## 线程池
### 一. 线程池解决的问题
#### 1.1 当需要创建大量线程时，为了降低系统资源消耗，可以复用线程来执行异步任务，并将一些溢出任务放入到阻塞队列中，等待中
空闲线程消费，线程池就是管理线程的创建，任务的执行和阻塞队列中任务的消费。
#### 1.2 为了适应多变的业务场景中，线程池可以按需配置，这些配置参数可以在创建线程池时指定，线程池也提供了一些钩子方法，允许用户
在运行时动态调整一些配置参数。
### 二. 线程池的主要参数配置
#### 2.1 核心线程数和最大线程数
#### 2.2 阻塞队列的类型，以及有界阻塞队列的容量
#### 2.3 拒绝策略
#### 2.4 空闲线程的存活时间，指定核心线程是否允许超时关闭
#### 2.5 核心线程的预热策略

### 三. 线程池的工作流程
#### 3.1 新任务到来的执行流程

### 四. 使用线程池需要注意的问题
#### 4.1 死锁问题
```txt
使用线程池特别要注意死锁问题，特别是提交给线程池的任务之间有依赖关系的，

```
#### 4.2 线程泄漏问题
#### 4.3 内存冲击