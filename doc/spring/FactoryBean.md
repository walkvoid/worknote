# springd的FactoryBean

## 特点
1.使用工厂bean的方式创建的bean需要调用FactoryBean.getObject()方法获取，换言之，这些bean并不会直接创建在spring容器中。

2.工厂bean是一个编程公约，它的实现并不依赖于注解驱动和反射。

3.往spring中注册了一个FactoryBean，取出的并不是这个FactoryBean本身，而是getObject方法所返回实例bean，spring在启动过程中会
自动调用getObject方法