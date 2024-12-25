# junit
### junit5的组成
JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage
- JUnit Platform: 基础组件,包含了junit-platform-commons，junit-platform-engine和junit-platform-launcher，commons包含了一些核心类，工具类，日志接口和异常等；
engine 则定义了测试引擎的接口，也包含了一个基础实现HierarchicalTestEngine。launcher是执行测试用来的入口，包含了一个核心入口Launcher。
- JUnit Jupiter：包含了三部分，junit-jupiter-api，junit-jupiter-engine和junit-jupiter-params。junit-jupiter-api中定义了编写测试用来
需要使用的常用注解，例如@Test，@TestTemplate等；还定义了一些扩展接口。junit-jupiter-engine包含了一个引擎JupiterTestEngine，它是HierarchicalTestEngine
引擎的增强实现。junit-jupiter-params是junit5测试用例的方法参数处理（在junit5中测试方法允许带参数），例如从外部csv文件中获取的参数。
- JUnit Vintage：junit4.12以前的测试引擎，在springboot中，如果使用了junit5版本的测试套件，需要将此依赖排除.因为JUnit Jupiter已经提供了JupiterTestEngine。

### junit5选择测试引擎
