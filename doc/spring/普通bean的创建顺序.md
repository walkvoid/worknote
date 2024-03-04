# 普通bean的创建顺序
## 简介
我们都知道在todo中，一个普通的bean会被一个BeanDefinition实例包裹，只有在AbstractApplicationContext.refresh里面的finishBeanFactoryInitialization
方法中，才会调用Bean的构造方法利用反射创建出这个Bean的实例。但是如果一个Bean依赖另外一个Bean，它的创建顺序是怎样的呢？
```java
@Service
public class UserService{
    //......
}
```
```java
@Service
public class OrderService{
    @Autowire
    public OrderService(UserService userService){
        System.out.println("OrderService init......");
    }
}

```
在上面的例子中，
