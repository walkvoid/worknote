# BeanPostProcessor
## 简介
是bean生命周期的一个钩子接口，准确来说是bean初始化期间的一个钩子函数，利用它可以很方便的修改bean，比如检查bean实现的接口，检查所标记的
注解和把一个bean转换成代理bean等等。

由于BeanPostProcessor可以有无数的实现类，spring也是循环逐个调用，所以BeanPostProcessor实现类可以指定调用顺序，一般是通过继承PriorityOrdered
接口和使用@Orderd注解来制定。

## 调用顺序
```java
public interface BeanPostProcessor {
    //在调用invokeInitMethods(beanName, wrappedBean, mbd)方法之前调用
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

    //在调用invokeInitMethods(beanName, wrappedBean, mbd)方法之后调用
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
```
```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
		implements AutowireCapableBeanFactory {
    protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        //......省略
        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            //逐个调用BeanPostProcessor所有实现类的postProcessBeforeInitialization方法
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }
    
        try {
            invokeInitMethods(beanName, wrappedBean, mbd);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(
                    (mbd != null ? mbd.getResourceDescription() : null),
                    beanName, "Invocation of init method failed", ex);
        }
        if (mbd == null || !mbd.isSynthetic()) {
            //逐个调用BeanPostProcessor所有实现类的postProcessAfterInitialization方法
            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }
        return wrappedBean;
	}	    
}
```

## 如何获取实现类
```java
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
}
```
可见BeanPostProcessor的所有实现类是提前set进去的，这里获取仅仅是返回beanPostProcessors属性。
set的地方有两个：
```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        //省略
        
        //1。在prepareBeanFactory里面beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this))
        //来添加BeanPostProcessor实现类
        prepareBeanFactory(beanFactory);

        try {
            //2.批量添加BeanPostProcessor实现类
            registerBeanPostProcessors(beanFactory);
            beanPostProcess.end();
        }
        catch (BeansException ex) {
            //省略
        }
    }
}
```
需要注意的是，BeanPostProcessor的实现类必须被spring管理才生效。

## invokeInitMethods方法的作用
invokeInitMethods方法作用很简单，就是调用InitializingBean的afterPropertiesSet方法，或者标记了@Bean("initMethod"=方法名)注解中
指定的方法，这两个方法又有个先后顺序问题，afterPropertiesSet方法先于@Bean("initMethod"=方法名)注解方法.

## 典型的例子
一个典型的例子就是springboot绑定配置的ConfigurationPropertiesBindingPostProcessor，它在postProcessBeforeInitialization做了绑定
属性的操作。这也是[@ConfigurationProperties注解的原理](../springboot/@ConfigurationProperties的原理.md)

## 内置的BeanPostProcessor识别
在上面的例子中，在ConfigurationPropertiesBindingPostProcessor中并未看到将其纳入spring管理的逻辑(比如加@Component注解)，那它是如何
注入到spring容器中的呢？
原来在我们的项目中，我们可能引用了其他组件，比如我们使用了redis，在redis的自动配置类中，有@EnableConfigurationProperties，在这个注解
上使用了@Import(EnableConfigurationPropertiesRegistrar.class)，我们在EnableConfigurationPropertiesRegistrar的registerBeanDefinitions
方法里面就有将ConfigurationPropertiesBindingPostProcessor作为bean注入到spring容器中的逻辑。
```java
class EnableConfigurationPropertiesRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        //调用静态方法registerInfrastructureBeans
        registerInfrastructureBeans(registry);
        registerMethodValidationExcludeFilter(registry);
        ConfigurationPropertiesBeanRegistrar beanRegistrar = new ConfigurationPropertiesBeanRegistrar(registry);
        getTypes(metadata).forEach(beanRegistrar::register);
    }
    static void registerInfrastructureBeans(BeanDefinitionRegistry registry) {
        //将ConfigurationPropertiesBindingPostProcessor注入到spring容器
        ConfigurationPropertiesBindingPostProcessor.register(registry);
        BoundConfigurationProperties.register(registry);
    }
}
```
所以当我们使用@ConfigurationProperties注解时，同时加上@EnableConfigurationProperties是比较推荐的，因为你不能总是依赖其他组件帮你
注入ConfigurationPropertiesBindingPostProcessor这个bean。