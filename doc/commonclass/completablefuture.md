# CompletableFuture的常见用法和原理

## 特点或者原理
### 1.xxx()方法和xxxAsync()方法的区别
xxxAsync()方法表示将任务提交到线程池中执行,默认使用ForkJoinPool线程池,也可以指定自定义的线程池;xxx()方法就在当前线程执行,换句话说和前
一个CompletableFuture执行的线程一致.
```text
//doB()方法执行的线程和doA()方法执行的线程相同
CompletableFuture.runAsync(()->doA()).thenRun(()->doB());
```

## 常见用法
### 1.同时执行任务A和B,AB都执行成功再执行任务C
```text
//方式1
@Test
public void testAllOf() throws InterruptedException, ExecutionException {
    final CalcResultDTO result = new CalcResultDTO();
    CompletableFuture.allOf(CompletableFuture.supplyAsync(()->doA(result)), CompletableFuture.supplyAsync(()->doB(result)))
            .thenAcceptAsync(x -> doC(result))
            .get(); //调用get方法目的是等待异步任务ABC都执行完
}
//方式2
@Test
public void testRunAfterBothAsync() throws InterruptedException, ExecutionException {
    final CalcResultDTO result = new CalcResultDTO();
    CompletableFuture.runAsync(()->doA(result)).runAfterBothAsync(CompletableFuture.supplyAsync(()->doB(result)),
            ()-> doC(result))
            .get();
}
```
### 2.同时执行任务A和B,AB有一个成功再执行任务C
```text
//方式1
@Test
public void testAnyOf() throws InterruptedException, ExecutionException {
    final CalcResultDTO result = new CalcResultDTO();
    CompletableFuture.anyOf(CompletableFuture.supplyAsync(()->doA(result)), CompletableFuture.supplyAsync(()->doB(result)))
            .thenAcceptAsync(x -> doC(result))
            .get(); //调用get方法目的是等待异步任务A/B,C都执行完
}
//方式2
@Test
public void testRunAfterEitherAsync() throws InterruptedException, ExecutionException {
    final CalcResultDTO result = new CalcResultDTO();
    CompletableFuture.runAsync(()->doA(result)).runAfterEitherAsync(CompletableFuture.supplyAsync(()->doB(result)),
            ()-> doC(result))
            .get();
}
```

### 3.thenCombine方法的使用
在上面的两个例子中,都是传入一个CalcResultDTO实例作为结果,在实际中我们可能并不是这样做的,现实中doA()方法会返回一个结果,doA()方法会返回另外
一个结果,这个时候我们就可以使用thenCombine将两个任务A和B的执行结果都作为任务C参数进行处理.
```text
@Test
public void testRunAfterEitherAsync() throws InterruptedException, ExecutionException {
    CompletableFuture.supplyAsync(()->doA(result)).thenCombine(CompletableFuture.supplyAsync(()->doB(result)),
        (doAResult,doBResult)-> doC(doAResult,doBResult))
        .get();
    //因为这里doA和doB都是放到xxxAsync方法中,所以这里任务ABC所在的线程都可能是不同的
}
```

### 3.thenCompose的使用
thenCompose方法可以处理前一个执行结果,然后返回一个新的CompletableFuture对象,与thenApply不同的是,这个新的CompletableFuture对象你可以
自由构造更加的灵活,而thenApply仅仅只专注代码逻辑的实现.
```text
@Test
public void testRunAfterEitherAsync() throws InterruptedException, ExecutionException {
    CompletableFuture.supplyAsync(()->doA(result)).thenCombine(CompletableFuture.supplyAsync(()->doB(result)),
        (doAResult,doBResult)-> doC(doAResult,doBResult))
        .get();
    //因为这里doA和doB都是放到xxxAsync方法中,所以这里任务ABC所在的线程都可能是不同的
}
```