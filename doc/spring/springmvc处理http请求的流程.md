# springmvc处理http请求的流程
## 流程
1.springboot内嵌的tomcat接收到请求，经过一系列的Filter后，将请求交给servlet处理，servlet再将请求交给spring处理。

2.首次交给spring处理的入口是:
```text
org.springframework.web.servlet.FrameworkServlet#service
注：DispatcherServlet是FrameworkServlet的子类，所以这里FrameworkServlet的具体类型是DispatcherServlet
```
根据请求方式的不同，会调用对应的doGet，doPost方法。当然不管是何种请求方式，最终还是会统一调用FrameworkServlet#processRequest方法。

3.调用最核心的getHandler方法
```text
#org.springframework.web.servlet.DispatcherServlet#getHandler
@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    if (this.handlerMappings != null) {
        for (HandlerMapping mapping : this.handlerMappings) {
            HandlerExecutionChain handler = mapping.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
    }
    return null;
}
```
这里会循环调用容器中注入的HandlerMapping集合中的HandlerMapping.getHandler方法，并返回一个HandlerExecutionChain，该执行链就是执行请求
的载体，为什么会有执行链，这是因为springmvc给我们提供了HandlerInterceptor接口，也就是我们说的拦截器，该执行链就是要执行那些我们自定义的拦截器方法。

## DispatcherServlet.handlerMappings赋值流程
1.现在我们所关心的是上面的DispatcherServlet#getHandler方法中的DispatcherServlet.handlerMappings，我们追踪handlerMappings使用地方，
看到了源头：
```text
#org.springframework.web.servlet.FrameworkServlet
private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FrameworkServlet.this.onApplicationEvent(event);
    }
}
```
FrameworkServlet的内部类实现了ApplicationListener接口，并且处理容器刷新事件ContextRefreshedEvent，这里是不是呢，其实不是，因为这里的
初始化依赖于事件源，通常的应用启动中并不会发布这个事件，那么上面的逻辑也就不会执行。

2.实际上执行的源头在应用接收的第一个请求的时候会初始化ServletBean，在这里会调用onFresh方法，进而获取合适的HandlerMapping：
```text
org.apache.catalina.core.StandardWrapper#initServlet
org.springframework.web.servlet.FrameworkServlet#initServletBean
org.springframework.web.servlet.FrameworkServlet#initWebApplicationContext
org.springframework.web.servlet.DispatcherServlet#onRefresh
org.springframework.web.servlet.DispatcherServlet#initHandlerMappings
```
在initHandlerMappings的方法中，实际上是获取了spring容器中所有的HandlerMapping类型的bean，而这些具体的bean又是在自动配置类WebMvcAutoConfiguration
中注册的。
```text
/** org.springframework.web.servlet.DispatcherServlet#initHandlerMappings **/
private void initHandlerMappings(ApplicationContext context) {
    this.handlerMappings = null;
    if (this.detectAllHandlerMappings) {
        // Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
        Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            AnnotationAwareOrderComparator.sort(this.handlerMappings);
        }
    }
    //...... 
}
```
