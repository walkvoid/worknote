# spring加载自定义的class

## 知识要点
1.spring会扫描@ComponentScan注解指定的包路径，读取路径下的class文件，然后判断符合条件的类，将其包装成bean注入到容器中。

2.有时候我们想让spring读取经过我们修改过的class文件（比如通过asm字节码工具），以满足我们的业务需求。

3.该代码的入口：
```
org.springframework.context.annotation.ComponentScanAnnotationParser#parse
org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan
MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
```
在getMetadataReaderFactory()中，会获取一个MetadataReaderFactory的工厂接口实例对象，该工厂接口有一个方法getMetadataReader(),
getMetadataReader方法就是返回一个MetadataReader实例，其中就包含了读取class文件的逻辑，所以我们只要注入一个自定义的MetadataReaderFactory
实例，然后在该自定义的MetadataReader读取修改后的class文件返回即可。

4.现在问题的关键是如何注入这个自定义的MetadataReaderFactory，在ClassPathScanningCandidateComponentProvider的set方法中我们看到了曙光。
```text
public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
    this.metadataReaderFactory = metadataReaderFactory;
}
```
但是其他地方并没有调用此set方法，所以我们需要使用spring框架给我们提供的xxxAware钩子方法，获取到这个ClassPathScanningCandidateComponentProvider
实例，然后手动调用这个setMetadataReaderFactory()将我们自定义的MetadataReaderFactory传进去。