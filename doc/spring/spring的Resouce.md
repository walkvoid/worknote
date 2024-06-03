# spring的Resource
## Resource接口


## classpath: 和 classpath*:
- classpath: 表示当前项目打包后的绝对路径(/path/projectpath/target/classes/)
```text
    @Test
    public void resourceTest() throws IOException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource resource = patternResolver.getResource("classpath:");
        log.info("resource,path:{}", resource.getURL());
        ///path/projectpath/target/test-classes/
    }
```
注意：运行测试代码取的是test-classes，而运行源码的才是classes。对于maven结构的项目，resources文件夹的文件打包后也会在/target/classes/下。

- classpath*: 表示当前项目classes,test-classes,jdk下的jar和所依赖的所有jar的路径
```text
    @Test
    public void resourceTest() throws IOException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = patternResolver.getResources(CLASSPATH_ALL_URL_PREFIX);
        for (Resource resource : resources) {
            log.info("resource，path：{}", resource.getURI());
        }
        //resource，path：file:/path/projectPath/target/test-classes/
        //resource，path：file:/path/projectPath/target/classes/
        //resource，path：jar:file:/C:/jdk1.8.0_181/jre/lib/charsets.jar!/
        //resource，path：jar:file:/C:/jdk1.8.0_181/jre/lib/deploy.jar!/
        //resource，path：jar:file:/C:/jdk1.8.0_181/jre/lib/ext/access-bridge-64.jar!/
        //resource，path：jar:file:/D:/apache-maven-3.5.3/repo/org/springframework/boot/spring-boot-starter/2.7.18/spring-boot-starter-2.7.18.jar!/
        //resource，path：jar:file:/D:/apache-maven-3.5.3/repo/org/springframework/boot/spring-boot/2.7.18/spring-boot-2.7.18.jar!/   
    }
```