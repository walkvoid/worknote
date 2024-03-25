# 动态代理

## java动态代理介绍
1.Proxy提供了一系列创建动态代理类实例的静态方法，同时它也是所有动态代理类的父类。

2.Proxy类中有一个InvocationHandler（执行句柄）类型的成员变量h，它是一个包含invoke方法的接口，所有代理类的方法执行都会被这个invoke方法拦截。

3.Proxy.newProxyInstance静态方法创建一个动态代理类，它的类名是com.sun.proxy.$Proxy1,$Proxy是固定前缀，1是整个jvm自增的数字。

4.Proxy创建动态代理类的底层依然是操作字节码来动态生成class文件，生成逻辑在sun.misc.ProxyGenerator#generateProxyClass里面。

5.Proxy创建只能创建接口类动态代理类,java.lang.reflect.Proxy$ProxyClassFactory.apply方法有做校验。

