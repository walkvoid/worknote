# @ConfigurationProperties的原理
## 简介
将该注解标注在java类上，可以将该类的属性和外部配置文件中的配置绑定起来，实现通过访问该类的属性就能访问到配置文件中的值。需要注意的是，该
类的属性必须提供set方法。
```java
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface ConfigurationProperties {
    //配置的前缀
	@AliasFor("prefix")
	String value() default "";
    
	@AliasFor("value")
	String prefix() default "";
    
    //是否忽略无效的属性（通常是错误的类型，例如java类上是数字，但是配置文件是一个字符串）
	boolean ignoreInvalidFields() default false;
    
    //是否忽略不存在的属性
	boolean ignoreUnknownFields() default true;

}
```

## 绑定流程调用关键方法
- org.springframework.boot.SpringApplication#run(java.lang.String...)
- org.springframework.boot.SpringApplication#refreshContext
- org.springframework.context.support.AbstractApplicationContext#refresh
- org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization
- org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons
- org.springframework.beans.factory.support.AbstractBeanFactory#getBean(java.lang.String)
- org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(String, RootBeanDefinition, Object[])
- org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(String, Object, RootBeanDefinition)
- org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization
- org.springframework.boot.context.properties.ConfigurationPropertiesBinder#bind
- org.springframework.boot.context.properties.bind.Binder#bindDataObject
- org.springframework.boot.context.properties.bind.JavaBeanBinder.BeanProperty#setValue

## 流程说明
1.整个属性绑定流程是嵌入在bean的创建流程中，所以这个java类也必须是spring的bean。
2.在初始化bean的的方法initializeBean中，如果bean不是合成bean(所谓合成bean，就是只非应用程序定义的，例如通过aop增强的bean就是一个
合成bean),就调用BeanPostProcessor的postProcessAfterInitialization方法。
3.这个BeanPostProcessor的实现类是ConfigurationPropertiesBindingPostProcessor，ConfigurationPropertiesBindingPostProcessor
是在refresh方法中的org.springframework.context.support.AbstractApplicationContext#registerBeanPostProcessors,需要注意的是，
在org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory也有添加BeanPostProcessor的操作。
postProcessAfterInitialization方法里面调用了ConfigurationPropertiesBinder的bind方法，这里就是实际的绑定入口。
4.在JavaBeanBinder的内部类BeanProperty中调用setValue方法完成属性的赋值。