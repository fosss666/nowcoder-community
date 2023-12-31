# 牛客论坛

### Spring

* @Component和@Configuration的区别

  >   **@Configuration 中所有带 @Bean 注解的方法都会被动态代理（cglib），因此调用该方法返回的都是同一个实例。而 @Conponent 修饰的类不会被代理，每实例化一次就会创建一个新的对象。** 

* @Mapper和@Repository区别

  >  **@Mapper是MyBatis的注解，@Repository是Spring中的注解，这些注解就是声明一个Bean。** 
  >
  >  1、@Mapper不需要配置扫描地址，可以单独使用，如果有多个mapper文件的话，可以在项目启动类中加入@MapperScan(“mapper文件所在包”)
  > 2、@Repository不可以单独使用，否则会报错误，要想用，必须配置扫描地址（@MapperScannerConfigurer）

* spring的三种注入方式

  + 属性注入

    ```java
    @Service
    public class BService {
        @Autowired
        AService aService;
        //...
    }
    ```

  + setter方法注入

    ```java
    @Service
    public class UserService {
        private Wolf3Bean wolf3Bean;
        
        @Autowired  //通过setter方法实现注入
        public void setWolf3Bean(Wolf3Bean wolf3Bean) {
            this.wolf3Bean = wolf3Bean;
        }
    }
    ```

  + 构造器注入

    ```java
    @Service
    public class UserService {
         private Wolf2Bean wolf2Bean;
        
         @Autowired //通过构造器注入
        public UserService(Wolf2Bean wolf2Bean) {
            this.wolf2Bean = wolf2Bean;
        }
    }
    ```

### SpringMVC

* thymeleaf模板需要将第二行标签改成<html lang="en" xmlns:th="http://www.thymeleaf.org">

### Mybatis

* mysql和mybatis配置

  ```yaml
  spring:
    thymeleaf:
      cache: false  # 关闭thymeleaf缓存
    datasource:  # 数据库配置
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        idle-timeout: 30000  # 空闲超时时间
        maximum-pool-size: 15 #最大连接数量
        minimum-idle: 5 #最少连接数
      type: com.zaxxer.hikari.HikariDataSource
      url: jdbc:mysql://192.168.60.129:3306/community?characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong
      username: root
      password: 123456
  mybatis:
    configuration:
      mapUnderscoreToCamelCase: true   #转驼峰
      useGeneratedKeys: true  #如果插入的表以自增列为主键，则允许 JDBC 支持自动生成主键，并可将自动生成的主键返回。
    mapper-locations: classpath:mapper/*.xml  #映射文件位置
    type-aliases-package: com.fosss.community.entity #配置文件中实体类不用写包名
  
  ```

* 注意mybatis-spring-boot-starter和springboot的版本对应关系

### 开发社区首页

### 项目调试技巧

### 版本控制

* 常用命令
  + git config --list   显示所有配置
  + git config --global user.name "用户名"  配置用户名
  + git config --global user.email "邮箱"  配置邮箱
  + git init  初始化
  + git status  查看不受git管理的文件
  + git add  星.xml/星.java/ .   添加到本地仓库
  + git commit -m '备注'  提交到暂存区
  + git push 远程仓库网址 分支      推送到远程仓库
  + ssh-keygen -t rsa -C "邮箱"   生成ssh密钥
  + git clone  仓库地址  克隆项目

### 事务管理

* 事务是由N步数据库操作序列组成的逻辑执行单元，这系列操作要么全执行，要么全放弃执行

* 事务的特性（ACID）

  + 原子性：事务是应用中不可再分的最小执行体
  + 一致性：事务执行的结果，需使数据从一个一致性状态，变为另一个一致性状态
  + 隔离性：各个事务的执行互不干扰，任何事务的内部操作对其他的事务都是隔离的
  + 持久性：事务一旦提交，对数据所做的任何改变都要记录到永久存储器中

* 事物的隔离性

  + 第一类丢失更新：某一个事务的回滚导致另一个事务已更新的数据丢失了

    第二类丢失更新：某一个事务的提交导致另一个事务已更新的数据丢失了

  + 脏读：某一个事务读取了另一个事务未提交的数据

  + 不可重复读：某一个事务，对同一数据前后读取的结果不一致

  + 幻读：某一个事务，对同一个表前后查询到的行数不一致

* 事务隔离级别与是否避免的并发异常

  ![image-20230911121032627](https://cdn.jsdelivr.net/gh/fosss666/notebook/img/202309111213692.png)

* 实现机制

  ![image-20230911121344790](https://cdn.jsdelivr.net/gh/fosss666/notebook/img/202309111213833.png)

### 私信列表

* sql的编写需要多理解，尤其是子查询的应用

### redis

* [redis常用操作](https://fosss666.github.io/2023/08/07/Redis%E5%B8%B8%E7%94%A8%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/index.html?_sw-precache=96f3eef89475d2b6ce0c83b4709705c0)
* java整合redis时，注意多次访问同一个key可以这样做：
```java
BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
```
* redis中的事务多使用编程实现，实现代码：

  ___如果要进行查询操作的话，应该放到事务之外执行，如放到multi方法执行之前。否则查询操作会等到事务提交之后才进行，导致查询的结果为空___
```java
Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
```

### kafka

* 用docker部署

### es

* 解决netty启动冲突

  ```java
  # 在启动类中加入
  @PostConstruct
  public void init() {
  // 解决netty启动冲突问题
  // Netty4Utils.setAvailableProcessors()
  System.setProperty("es.set.netty.runtime.available.processors", "false");
  }
  ```


### 热帖排行

添加帖子、加精、添加评论、点赞，这几个操作会触发分数重新计算操作，如果发生以上行为，则将帖子id存入redis中的set，标记需要进行分数刷新操作，然后用quartz定时任务进行刷新

#### 生成 长图

下载安装wkhtmltopdf

在bin目录下执行命令，或将bin配置到环境变量中

* wkhtmltopdf  网页路径  存放路径/name.pdf
* wkhtmltoimage  网页路径 【--quality 75】  存放路径/name.png    # --quality 75为压缩图片

用java调用：

```java
Runtime.getRuntime.exec("命令");
```

