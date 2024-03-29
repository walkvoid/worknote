# 线程池
## 一. 线程池解决的问题
- 1.1 当需要创建大量线程时，为了降低系统资源消耗，可以复用线程来执行异步任务，并将一些溢出任务放入到阻塞队列中，等待空闲线程消费，线程池就是管理线程
的创建，任务的执行和阻塞队列中任务的消费。
- 1.2 为了适应多变的业务场景中，线程池可以按需配置，这些配置参数可以在创建线程池时指定，线程池也提供了一些钩子方法，允许用户在运行时动态调整一些配置参数。
## 二. 线程池的主要参数配置
- 2.1 核心线程数和最大线程数
```text
corePoolSize:核心线程数，当线程池中线程数量小于该值时，线程池总是会创建新的线程来执行提交的任务。
maximumPoolSize：最大线程数，当异步任务添加到阻塞队列失败时，线程池会创建新的线程来执行提交的任务,直到正在执行的线程数量等于该值。
```
- 2.2 阻塞队列的
```text
BlockingQueue<Runnable> workQueue: 阻塞队列，BlockingQueue是接口，无默认值，在创建线程池的时候必须手动指定。
```
- 2.3 拒绝策略
```text
RejectedExecutionHandler handler:当阻塞队列满了，并且线程数已经到达了maximumPoolSize，则会执行该handler的rejectedExecution方法，提供了
四种拒绝策略（当然也可以自定义）：
1.AbortPolicy：默认值，执行抛出异常，拒绝执行任务
2.DiscardPolicy：直接丢弃任务，不会抛出异常
3.DiscardOldestPolicy：先把把队列首个任务丢弃，然后直接执行刚提交的任务
4.CallerRunsPolicy：直接在当前提交任务的线程执行此任务
```
- 2.4 空闲线程的存活时间，核心线程是否允许超时关闭
```text
keepAliveTime:超过核心线程数的那部分线程等待死亡的时间，单位是nanoseconds
allowCoreThreadTimeOut：是否允许核心线程空闲死亡，如果设置为了true，核心线程空闲等待了keepAliveTime时间后，将会死亡。
```
- 2.5 核心线程的预热策略
```text
prestartCoreThread():开启预热一个线程
ensurePrestart():开启至少一个预热线程，哪怕核心线程数设置为0
prestartAllCoreThreads(): 预热所有的核心线程
```

## 三. 线程池的工作流程
### 3.1 新任务到来的执行流程

## 四. 使用线程池需要注意的问题
### 4.1 死锁问题
```txt
使用线程池特别要注意死锁问题，特别是提交给线程池的任务之间有依赖关系的，

```
### 4.2 线程泄漏问题
### 4.3 内存冲击