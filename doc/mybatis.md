# mybatis的介绍

## 回顾原生jdbc的使用
```text
    // 1.注册驱动，mysql8的驱动可以自动注册
    //Class.forName("com.mysql.cj.jdbc.Driver");
    // 2.获取连接
    Connection connection = DriverManager.getConnection(url, "root", "123456");
    // 3.获取statement对象
    PreparedStatement preparedStatement = connection.prepareStatement("select  * from `user`");
    // 4.执行sql
    ResultSet resultSet = preparedStatement.executeQuery();
    // 5.结果处理
    while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("user_name");
        int age = resultSet.getInt("age");
        System.out.println("id: " + id + ", user_name: " + name + ", age: " + age);
    }
    // 6.关闭连接
    preparedStatement.close();
    connection.close();
```

## 原生mybatis的使用
```text
    1.获取文件配置文件
    InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config-lls.xml");
    2.根据配置信息构造SqlSessionFactory对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    3.获取SqlSession对象
    SqlSession sqlSession = sqlSessionFactory.openSession();
    4.获取mapper接口的代理对象
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    5.执行sql操作
    List<User> users = mapper.selectAll();
    users.forEach(System.out::println);
    6.关闭资源
    sqlSession.close();
```
### 构建SqlSessionFactory
构建的过程十分简单，SqlSessionFactory的就一个Configuration的属性，解析配置XMLConfigBuilder#parse然后赋值给Configuration就行了，最主要的
逻辑在解析xml的逻辑中：
```text
    #org.apache.ibatis.builder.xml.XMLConfigBuilder#parseConfiguration
    propertiesElement(root.evalNode("properties"));
    Properties settings = settingsAsProperties(root.evalNode("settings"));
    loadCustomVfs(settings);
    loadCustomLogImpl(settings);
    typeAliasesElement(root.evalNode("typeAliases"));
    pluginElement(root.evalNode("plugins"));
    objectFactoryElement(root.evalNode("objectFactory"));
    objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
    reflectorFactoryElement(root.evalNode("reflectorFactory"));
    settingsElement(settings);
    // read it after objectFactory and objectWrapperFactory issue #631
    environmentsElement(root.evalNode("environments"));
    databaseIdProviderElement(root.evalNode("databaseIdProvider"));
    typeHandlerElement(root.evalNode("typeHandlers"));
    //这一步很关键，解析关联的外部mapper.xml文件，并为每个Mapper接口生成代理类。
    mapperElement(root.evalNode("mappers"));
```
### 使用SqlSessionFactory获取SqlSession