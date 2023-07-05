# ApplicationContextInitializer
## 简介
springboot的初始化器加载使用在spring的整个启动过程中在比较靠前的位置，他在org.springframework.boot.SpringApplication#prepareContext中，
此方法是先于refresh方法的。
```java
public class SpringApplication {
    //调用顺序
    public ConfigurableApplicationContext run(String... args) {
        //......省略	
        refreshContext(context);
        afterRefresh(context, applicationArguments);
        //......省略
        return context;
    }

    //初始化器的加载使用
    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
                    ApplicationContextInitializer.class);
            Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
            initializer.initialize(context);
        }
    }
}

```

## 查找initializers
查找initializers在SpringApplication的构造方法中
```java
public class SpringApplication {
    public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        //省略
        //set初始化器
        setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = deduceMainApplicationClass();
    }
}
```

## getSpringFactoriesInstances方法
在springboot的启动流程中，getSpringFactoriesInstances方法是一个重要的方法，它主要是负责加载并实例化指定接口类型的子类，这些类型它是
配置在springboot的配置文件META-INF/spring.factories中的，所以如果有需要的话，我们是可以自定义我们自己的初始化器的。
```text
#spring-boot-2.7.13.jar!/META-INF/spring.factories
# Application Context Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
org.springframework.boot.context.ContextIdApplicationContextInitializer,\
org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
org.springframework.boot.rsocket.context.RSocketPortInfoApplicationContextInitializer,\
org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
```

```text
# spring-boot-autoconfigure-2.7.13.jar!/META-INF/spring.factories
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener
```
