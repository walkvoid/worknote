## maven插件开发实战
#### 插件开发所需的依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.mavendemo1</groupId>
    <artifactId>conflictcheck-maven-plugin</artifactId>
    <packaging>maven-plugin</packaging>
    <version>1.0.0</version>
    <name>conflictcheck-maven-plugin</name>

    <dependencies>
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-plugin-api</artifactId>
            <version>3.5.4</version>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.plugin-tools</groupId>
            <artifactId>maven-plugin-annotations</artifactId>
            <version>3.5.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-core</artifactId>
            <version>3.5.4</version>
        </dependency>
    </dependencies>

  <build>
      <pluginManagement>
          <plugins>
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-plugin-plugin</artifactId>
                  <version>3.5</version>
              </plugin>
          </plugins>
      </pluginManagement>
  </build>
</project>
```
- 命名：对于非官方的maven插件，采用 "插件名-maven-plugin"的方式，而maven官方的插件使用"maven-plugin-xxx"的命名方式。
- \<packaging>maven-plugin\</packaging>：插件的打包方式是maven-plugin。
- 开发maven插件核心的依赖有maven-plugin-api，maven-core和maven-plugin-annotations(jdk1.5前是不支持注解的，所以开发maven插件的
需要核心注解都在maven-plugin-annotations)。

#### 插件demo
```java
@Mojo(name = "conflict-check", defaultPhase = LifecyclePhase.COMPILE)
public class ConflictCheckMojo extends AbstractMojo {
    
    @Parameter(name = "authorName", defaultValue = "")
    private String authorName;
    @Parameter(name = "projectBasedir", defaultValue = "${project.basedir}")
    private String projectBasedir;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${reactorProjects}", required = true, readonly = true)
    protected List<MavenProject> reactorProjects;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("===============> start depends conflict check ============");
        getLog().info("===============> authorName:"+authorName+"============");
        getLog().info("===============> projectBasedir:"+projectBasedir+"============");
        List<Dependency> dependencies = project.getDependencies();
        ProjectBuildingRequest projectBuildingRequest = project.getProjectBuildingRequest();

        ArtifactFilter artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_TEST);
        ProjectBuildingRequest buildingRequest =
                new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(project);
        DependencyNode dependencyNode = null;
        try {
            dependencyNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, artifactFilter);
        } catch (DependencyGraphBuilderException e) {
            throw new RuntimeException(e);
        }
        getLog().info("===============> dependencyNode ArtifactId:"+dependencyNode.getArtifact().getArtifactId()+"============");

    }
}
```
上面是一个插件的例子，命名叫ConflictCheck是因为想开发一个检查项目中依赖冲突的插件，当然只是命名叫这个，实际的代码还未实现，它只是一个
实验性的例子，在这个例子中，有一些点是在刚接触插件开发需要注意的值得注意的：
- 继承AbstractMojo： maven建议所有的插件都必修继承AbstractMojo，它只有一个抽象方法execute()需要实现。
- @Mojo(name = "conflict-check", defaultPhase = LifecyclePhase.COMPILE)：指明这是插件的一个可执行的goal，其中name
属性是必须的，defaultPhase则表示默认绑定的生命周期。
- @Parameter：注入maven中自带的一些对象，maven也是一个DI（依赖注入）框架，其中defaultValue也是支持表达式的。
- @Component： 当我们的插件需要第三方的jar时，我们可以使用@Component注解，它和@Parameter的功能是类似的，只不过是区分了注入对象的来源。
在例子中，我们DependencyGraphBuilder的来源于maven-dependency-tree：
```text
 <dependency>
    <groupId>org.apache.maven.shared</groupId>
    <artifactId>maven-dependency-tree</artifactId>
    <version>3.2.1</version>
</dependency>
```
#### 插件的使用

```text
<plugin>
    <groupId>com.mavendemo1</groupId>
    <artifactId>conflictcheck-maven-plugin</artifactId>
    <version>${revision}</version>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>conflict-check</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <authorName>walkvoid</authorName>
        <projectBasedir>${project.basedir}</projectBasedir>
    </configuration>
</plugin>
```
- 一个插件可以包含多个goal，比如maven官方的install插件就包含了help，install和install-file三个goal，所以在<executions>标签下我们可
以执行一个goal。
- 对于我们自定义的参数，我们放在<configuration>标签下，标签名就是我们的参数名。

#### 插件开发debug
    public void execute() throws MojoExecutionException, MojoFailureException {
插件开发的dubug和我们写普通的业务代码不同，借助IDEA工具，现在maven插件的execute()方法里代码打上断点，
然后在右侧的maven管理页面找到一个使用了该插件的项目，在Plugins目录下找到要调试插件的goal，右键点击"Debug 'xxx''"，执行后就会发现已经
执行到断点处了。

#### maven中的核心对象
```java
//每次执行maven都称为一个session，这里的执行指的是：mvn clean，maven deploy这些指令。
public class MavenSession implements Cloneable {
    //执行session的请求，包含了一些执行的参数，本地仓库的地址，本地maven的配置等等
    private MavenExecutionRequest request;

    //执行的结果，包含一些输出的信息（包括执行错误时的异常信息）
    private MavenExecutionResult result;

    private RepositorySystemSession repositorySession;

    //执行的的参数（例如：java的版本，环境变量等）
    private Properties executionProperties;

    //在maven多模块项目中，当前执行的MavenProject（单模块项目也是一样的）
    private MavenProject currentProject;
    
    //本次执行所包含的MavenProject集合
    private List<MavenProject> projects;

    //所有执行所包含的MavenProject集合
    private List<MavenProject> allProjects;

    //在多模块的项目中，最顶级的MavenProject
    private MavenProject topLevelProject;

    //项目的依赖图
    private ProjectDependencyGraph projectDependencyGraph;

    //是否是并行执行
    private boolean parallel;
}
```
```java
//和pom.xml一一对应，描述了pom.xml的标签信息
public class MavenProject implements Cloneable {
    // model实例
    private Model model;
    // 父MavenProject
    private MavenProject parent;
    //pom.xml文件句柄
    private File file;
    //项目基础文件夹句柄
    private File basedir;
    //
    private xxxArtifactxxx resolvedArtifacts;
    
}
```






#### 参考
[官方文档](https://maven.apache.org/guides/introduction/introduction-to-plugins.html)