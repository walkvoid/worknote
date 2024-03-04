### aop的执行流程
#### 前置
在spring容器启动时,需要代理bean会被包装成一个代理类注入的容器中,例如一个UserService(其类上有一个@Cachable注解),注入到spring中是一个UserService$$EnhancerBySpringCGLIB代理bean,
这个代理bean有很多个CGLIB$CALLBACK_n(n表示数字0,1,2,...),按职能区分,这些CGLIB$CALLBACK_有的负责静态方法,有的负责代理Equals方法,有的负责hashcode方法等,
其中有一个负责普通方法的动态拦截处理,需要说明的是,这些CGLIB$CALLBACK_n都是对应着CglibAopProxy的内部类实例.在执行到一个普通方法时,实际会调用
CglibAopProxy.DynamicAdvisedInterceptor#intercept方法.
```text
//CglibAopProxy.DynamicAdvisedIntercepto.class
//参数proxy: 就是UserService$$EnhancerBySpringCGLIB实例
//参数method: 所执行的方法
//参数args:所执行方法的参数
//参数methodProxy:就是执行方法的代理类
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {}

```
CglibAopProxy.DynamicAdvisedInterceptor.class还有一个重要的属性AdvisedSupport, 它有总要的子类ProxyFactory,在这个例子中改属性
就是一个ProxyFactory实例,在ProxyFactory实例有一个advisors属性,它是一个list,包含了很多"织入",一个织入可以认为是一个切面.在这个例子
中,它包含了一个BeanFactoryCacheOperationSourceAdvisor,BeanFactoryCacheOperationSourceAdvisor有一个CacheInterceptor,这个CacheInterceptor
就是处理缓存相关的.
### 1.获取MethodInterceptor集合
```text
org.springframework.aop.framework.AdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice
```

