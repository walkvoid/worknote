### 变量汇总
####内置变量属性
```text
${basedir}表示项目的根路径，即包含pom.xml文件的目录
${version}表示项目版本
${project.basedir}同${basedir}
${project.baseUri}表示项目文件地址
${maven.build.timestamp}表示项目构建开始时间
${maven.build.timestamp.format}表示${maven.build.timestamp}的展示格式，默认值为yyyyMMdd-HHmm
```
#### pom变量属性
```text
${project.build.sourceDirectory}表示主源码路径，默认为src/main/java/
${project.build.testSourceDirectory}表示测试源码路径，默认为src/test/java/
${project.build.directory}表示项目构建输出目录，默认为target/
${project.outputDirectory}表示项目测试代码编译输出目录，默认为target/classes/
${project.groupId}表示项目的groupId
${project.artifactId}表示项目的artifactId
${project.version}表示项目的version，同${version}
${project.build.finalName}表示项目打包输出文件的名称,默认为${project.artifactId}${project.version}
```
#### setting.xml中的变量属性
```text
${settings.localRepository}表示本地仓库的地址
```
#### java系统属性
```text
//mvn help:system 可以查看所有的Java属性
${user.home} 表示用户目录
```

#### 本地环境变量的属性
```text
//mvn help:system 可以查看所有系统属性
${env.JAVA_HOME} 环境变量中JAVA_HOME的值
```

#### 自定义变量
```text
//pom.xml

<properties>
    <mysql.version>5.6.32</mysql.version>
    <my.project.name>maven-demo1<my.project.name>
</properties>
```

### 变量的使用
maven中变量默认只在pom文件中使用,如果要扩大变量的使用范围,比如在我们的配置文件中application.properties中使用,需要使用下面的配置:
```text
//pom.xml
<build>
    <resources>
      <resource>
        <filtering>true</filtering> //设置成true,表示允许扩展变量的使用范围
        <directory>${project.basedir}/src/main/resources</directory> //指定变量使用的文件夹
        <includes>
          <include>*.properties</include> //更细粒度的控制变量使用的文件类型,包含的文件类型
        </includes>
        <excludes>
          <exclude>*.txt</exclude> //更细粒度的控制变量使用的文件类型,排除的文件类型
        </excludes>
      </resource>
    </resources>
</build>
```
使用变量的地方使用${变量属性名},例如
```text
//application.properties
spring.application.name=${my.project.name}
```

在多模块嵌套的项目中,同一版本的定义参考:
</br>[intellij网站的提问](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000439484-properties-in-parent-definition-are-prohibited-on-mac-osx-Intellij-2018-1-2)
</br>[intellij网站的提问的问题跟踪](https://youtrack.jetbrains.com/issue/IDEA-179451)
</br>[maven官网的解释](https://maven.apache.org/maven-ci-friendly.html)
</br>简而言之,使用模块构建多模块嵌套项目中,<paren>标签中只有三个属性能使用,分别是${revision}, ${sha1} 和${changelist},这几个属性还仅仅实在
项目的编译期间生效,如果需要install和deploy期间生效,则需要在使用上面三个属性的pom.xml加入:
```xml
 <build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>flatten-maven-plugin</artifactId>
            <version>1.1.0</version>
            <configuration>
                <updatePomFile>true</updatePomFile>
                <flattenMode>resolveCiFriendliesOnly</flattenMode>
            </configuration>
            <executions>
                <execution>
                    <id>flatten</id>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>flatten</goal>
                    </goals>
                </execution>
                <execution>
                    <id>flatten.clean</id>
                    <phase>clean</phase>
                    <goals>
                        <goal>clean</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### maven父子项目的关联
在多模块项目中,我们可以在父项目中定义下属项目,例如:
```text
//paren pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.mavendemo1</groupId>
  <artifactId>maven-demo1</artifactId>
  <packaging>pom</packaging>
  <version>${revision}</version>
  <name>maven-demo1</name>

  <modules>
    <module>module1/module1-api/module1-user-api</module>
    <module>module1/module1-service/module1-user-service</module>
  </modules>
</project>
```
我们使用相对路径来找到需要关联的子模块,在子模块中,我们同样需要声明继承的父模块:
```text
//child pom.xml
<parent>
    <groupId>com.mavendemo1</groupId>
    <artifactId>maven-demo1</artifactId>
    <version>${revision}</version>
    <relativePath>../../../pom.xml</relativePath>
</parent>

<artifactId>module1-user-api</artifactId>
<packaging>jar</packaging>
<version>${revision}</version>
<name>maven-demo1-module1-user-api</name>
```


