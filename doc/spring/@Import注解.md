# @Import注解
## 简介

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    
	Class<?>[] value();

}
```

@Import注解一般是标注在配置类上（带@Configuration注解的类）和其他注解上组成复合注解，它有一个Class类型数组的参数，这个Class类型可以
分以下几个类：

## 1.一个普通的类
引入后，会把这个类作为普通bean注入到spring容器中，相当于在这个类上加上@Component注解,这种场景很少使用。
```java

@SpringBootApplication
@Import(UserService.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CedisApplication.class, args);
    }
}
```

## 2.一个其他的配置
引入后，会把这个配置类注入到当前spring容器中。
```java
//在这里org.springframework.boot.autoconfigure.http; 
@AutoConfiguration(
        after = { GsonAutoConfiguration.class, JacksonAutoConfiguration.class, JsonbAutoConfiguration.class })
@ConditionalOnClass(HttpMessageConverter.class)
@Conditional(NotReactiveWebApplicationCondition.class)
@Import({ JacksonHttpMessageConvertersConfiguration.class, GsonHttpMessageConvertersConfiguration.class,
        JsonbHttpMessageConvertersConfiguration.class })
public class HttpMessageConvertersAutoConfiguration {
    //省略
}
```

## 3.一个ImportSelector接口的实现类
```text
@AutoConfiguration(after = { HibernateJpaAutoConfiguration.class, TaskExecutionAutoConfiguration.class })
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(JpaRepository.class)
@ConditionalOnMissingBean({ JpaRepositoryFactoryBean.class, JpaRepositoryConfigExtension.class })
@ConditionalOnProperty(prefix = "spring.data.jpa.repositories", name = "enabled", havingValue = "true",
		matchIfMissing = true)
@Import(JpaRepositoriesImportSelector.class)
public class JpaRepositoriesAutoConfiguration {
//省略
}
```

```java
public interface ImportSelector {

	String[] selectImports(AnnotationMetadata importingClassMetadata);

	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}
}
```
ImportSelector接口有一个selectImports方法，返回一个字符串数组，存放的是需要注入bean的全限定类名，spring会自动将这个类注入到spring容器中。

## 4.一个ImportBeanDefinitionRegistrar接口的实现类
```text
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MapperScannerRegistrar.class)
@Repeatable(MapperScans.class)
public @interface MapperScan {
    //省略
}
```
```java
public interface ImportBeanDefinitionRegistrar {
    
	default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, 
                                         BeanDefinitionRegistry registry,
                                         BeanNameGenerator importBeanNameGenerator) {
		registerBeanDefinitions(importingClassMetadata, registry);
	}

	default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                         BeanDefinitionRegistry registry) {
	}
}
```
这个接口自由化程度更高，我们可以按照自己的逻辑，将自己需要的bean注入到spring容器中，例如mybatis将mapper接口的代理类注入到spring容器。

## 原理
查看@Import注解使用的地方，可以定位到org.springframework.context.annotation.ConfigurationClassParser#processImports方法有处理
@Import注解的逻辑。给这个方法打断点，分析调用栈，可以看到调用顺序:
```text
1.org.springframework.context.support.AbstractApplicationContext#refresh
2.org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors
3.org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors
4.org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors
5.org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry
6.org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions
7.org.springframework.context.annotation.ConfigurationClassParser#parse
8.org.springframework.context.annotation.ConfigurationClassParser#processImports
```
1.这里我们主要关注ConfigurationClassPostProcessor实现的接口类BeanDefinitionRegistryPostProcessor,此接口还有一个父接口是BeanFactoryPostProcessor。
```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
```
```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {
    
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
```
在spring的bean生命周期中还有一个类似的接口BeanPostProcessor，BeanFactoryPostProcessor执行是先于BeanPostProcessor的，BeanFactoryPostProcessor
主要是 更改bean定义的（即BeanDefinition的子类，spring创建bean的时候，会先将bean包装成一个BeanDefinition的实例），此时bean还没有实例化，
我们可以调整BeanDefinition来达到我们想要的目的，BeanFactoryPostProcessor典型的实现就是mybatis的MapperScannerConfigurer类，它的作用是
将Mapper都转换成MapperFactoryBean注入到spring容器中。如果我们需要改变bean实例（非BeanDefinition实例），我们就需要实现BeanPostProcessor
的接口方法做一些处理，因为只有在此时bean实例才创建完成了。

调用的先后顺序先调用BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法，再调用BeanFactoryPostProcessor
的postProcessBeanFactory方法。

2.关于ConfigurationClassPostProcessor如何被找到
```text
1.org.springframework.boot.SpringApplication#run
2.org.springframework.boot.SpringApplication#prepareContext
3.org.springframework.boot.SpringApplication#applyInitializers
4.org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer#initialize
5.org.springframework.context.ConfigurableApplicationContext#addBeanFactoryPostProcessor
```
ConfigurationClassPostProcessor其实是被spring的初始化器的SharedMetadataReaderFactoryContextInitializer添加的，有关于springboot
的初始化器接口ApplicationContextInitializer，我们专门会讲。

3.注意，8.org.springframework.context.annotation.ConfigurationClassParser#processImports中，针对@Import引入了ImportBeanDefinitionRegistrar
的子类，processImports方法中仅仅是做一下解析并实例化ImportBeanDefinitionRegistrar实现类，真正的处理在
org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions，具体的实现在此方法里面的
org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsFromRegistrars，我们可以
看到在里面调用了org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions方法。