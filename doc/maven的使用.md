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

在多模块嵌套的项目中,统一版本的定义参考:
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

### 复用其他项目的dependencies
```
#比如我们想复用springboot中的依赖
<dependencyManagement>
<dependencies>
    <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-dependencies</artifactId>
	<version>${spring-boot.version}</version>
	<type>pom</type>
	<scope>import</scope>
    </dependency>
</dependencies>
</dependencyManagement>
```

### 常用插件:spring-boot-maven-plugin
```text
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>2.7.9</version> //版本建议和springboot的版本保持一致
            <executions>
			    <execution>
			        <phase>package</phase>
			        <goals>
		                <goal>repackage</goal>
			        </goals>
			    </execution>
		    </executions>
		    <configuration>
                <mainClass>com.aaa.bbb.DemoApplication</mainClass> //有多个主类时，指定主类
                <layout>ZIP</layout> //指定生成包的格式，默认是jar
                <<skip>true</skip>> //生成一个普通的jar包
            </configuration>
        </plugin>
    </plugins>
</build>
```
spring-boot-maven-plugin与maven默认打包插件不同主要有：
1. spring-boot-maven-plugin会将项目所使用的依赖jar都放到jar包里，这也是打出来的jar体积偏大(动则几百mb，普通的jar一般只有几mb)。
2. spring-boot-maven-plugin所生成的jar是可执行的jar，即能使用java -jar *.jar 的方式启动，主要是jar包里的主配置清单信息不同
```text
## jar包名/META-INF/MANIFEST.MF
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Built-By: jiangjunqing
Created-By: Apache Maven 3.5.4
Build-Jdk: 1.8.0_352
```
```text
# jar包名/META-INF/MANIFEST.MF
Manifest-Version: 1.0
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Archiver-Version: Plexus Archiver
Built-By: jiangjunqing
Spring-Boot-Layers-Index: BOOT-INF/layers.idx
Start-Class: com.aaa.bbb.DemoApplication
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Version: 2.7.9
Created-By: Apache Maven 3.5.4
Build-Jdk: 1.8.0_352
Main-Class: org.springframework.boot.loader.JarLauncher
```
### 常用插件:maven-source-plugin
```text
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>3.0.1</version>
    <configuration>
          <outputDirectory>/absolute/path/to/the/output/directory</outputDirectory> //生成源码的路径，默认是target下
          <finalName>filename-of-generated-jar-file</finalName> //更改生成源码的文件名，默认是*-sources.jar
          <attach>true</attach> //true 将上传到本地仓库或者远程私服
    </configuration>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>jar-no-fork</goal> //与jar项目，verify之前解阶段智只会执行一次，jar会执行两次
            </goals>
        </execution>
    </executions>
</plugin>
```
```text
<profiles>
<profile>
  <id>release</id>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>3.3.0</version>
        <executions>
          <execution>
            <id>attach-sources</id>
            <goals>
              <goal>jar-no-fork</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</profile>
</profiles>
```

### 常用插件:maven-deploy-plugin
```text
<plugin>
    <artifactId>maven-deploy-plugin</artifactId>
    <version>2.8.2</version>
    <configuration>
        <skip>true</skip> //此项目不参与deploy
    </configuration>
</plugin>
```
### 常用插件:maven-compiler-plugin
```text
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.6.1</version>
    <configuration>
        <source>1.8</source> //指定源码的java版本 等同于在设置属性：<maven.compiler.source>1.8</maven.compiler.source>
        <target>1.8</target> //指定class文件的java版本 等同于设置属性：<maven.compiler.target>1.8</maven.compiler.target>
    </configuration>
</plugin>          
```

### 常用插件:maven-resources-plugin
```text
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>handle mapper xml</id>
            <phase>process-sources</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${basedir}/target/classes</outputDirectory>
                <resources>
                    <resource>
                        <directory>${basedir}/src/main/java</directory> //xml文件打包的目录
                        <includes>
                            <include>**/*.xml</include> //指定xml文件也参与打包
                        </includes>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```
### 常用插件:maven-checkstyle-plugin
代码风格检查插件
```text
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.1</version>
    <configuration>
        <configLocation>config/checkstyle.xml</configLocation> //检查规则文件路径，如果不配置默认使用sun_check.xml
    </configuration>
    <executions>
        <execution>
            <id>checkstyle</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <failOnViolation>true</failOnViolation> //检查不通过将中断编译流程，打印错误
            </configuration>
        </execution>
    </executions>
</plugin>
```
详细的检查规则见[github仓库](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml)
