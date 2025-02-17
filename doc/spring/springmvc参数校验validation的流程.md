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
        validateIfApplicable(binder, parameter);//参数校验
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

## 哪些校验注解能被spring-mvc识别
- @Validated和@Valid注解
- 注解上有带元注解@Validated的
- 注解的命名是@Validxxx的，即带Valid前缀的
```text
//org.springframework.validation.annotation.ValidationAnnotationUtils#determineValidationHints
@Nullable
public static Object[] determineValidationHints(Annotation ann) {
        // Direct presence of @Validated ?
        if (ann instanceof Validated) {
        return ((Validated) ann).value();
        }
        // Direct presence of @Valid ?
        Class<? extends Annotation> annotationType = ann.annotationType();
        if ("javax.validation.Valid".equals(annotationType.getName())) {
        return EMPTY_OBJECT_ARRAY;
        }
        // Meta presence of @Validated ?
        Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
        if (validatedAnn != null) {
        return validatedAnn.value();
        }
        // Custom validation annotation ?
        if (annotationType.getSimpleName().startsWith("Valid")) {
        return convertValidationHints(AnnotationUtils.getValue(ann));
        }
        // No validation triggered
        return null;
}
```

## 如何启动注解校验
启用的大前提是将hibernate的依赖引入到pom中，此时javax的validation模块会通过spi的方式将hibernate-validation自动加载到项目中，进而spring-mvc
识别到并使之在项目中生效。
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>适配项目springboot版本即可</version>
</dependency>

```
- **当校验的参数是对象时，使用@Validated和@Valid注解标注在方法参数上即可。**
```java
@RestController
public class ValidationTestController {
    /**
     * 1.@Valid和@Validated相当于校验启用开关，标注在方法参数上
     * 2.当需要校验的参数是一个嵌套对象时，需要在嵌套对象中上加@Valid
     * @param validationVO1
     * @return
     */
    @PostMapping("/test2")
    public String test2(@RequestBody @Validated ValidationVO1 validationVO1){
        return "test2";
    }
}
```
针对controller不同的方法，spring有不同的HandlerMethodArgumentResolver，例如带@RequestBody注解的方法，springmvc会使用RequestResponseBodyMethodProcessor来处理。


- **当校验的参数原始类型时，需要在controller上加上@Validated注解（不能是@Valid）。**
```java
@RestController
@Validated
public class ValidationTestController {
    /**
     * 当校验的参数是原始类型时，只能在ValidationTestController类上加@Validated，校验才生效
     * @return
     */
    @GetMapping("/test1")
    public String test1(@RequestParam("name") @NotEmpty @Length(max = 11) String name){
        return "test1";
    }
}
```
在不加@Validated的情况下，springmvc使用的是RequestParamMethodArgumentResolver来处理， 但是这个resolver并没有参数校验的逻辑。
但是如果加了@Validated则是在org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle拦截器中处理的。

