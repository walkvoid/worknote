# springmvc参数校验validation的流程
## 流程
从springmvc处理http请求的流程文章中我们知道了一个请求进来，springmvc是如何处理并路由到具体的方法的。参数校验就在执行具体的controller
方法的步骤中，具体处理参数的方法中：
org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#resolveArgument

```text
//org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#resolveArgument
if (binderFactory != null) {
    WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
    if (arg != null) {
        validateIfApplicable(binder, parameter);
        if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
            throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
        }
    }
}
```
这里首先从WebDataBinderFactory工厂中获取一个WebDataBinder，WebDataBinder的父类DataBinder有一个属性validators
```java
public class DataBinder implements PropertyEditorRegistry, TypeConverter {
    private final List<Validator> validators = new ArrayList<>();
}
```
在运行中spring会将一个Validator的子类org.springframework.boot.autoconfigure.validation.ValidatorAdapter实例添加到validators中，
ValidatorAdapter里边会通spi的方式加载org.hibernate.validator.internal.engine.ValidatorImpl从而完成参数的校验。
```text
hibernate-validator-6.2.5.Final.jar
    |-META-INF
        |-services
            |-javax.validation.spi.ValidationProvider      
```

