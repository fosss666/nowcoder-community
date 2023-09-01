# 牛客论坛

## 第一章 社区首页

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
      url: jdbc:mysql://192.168.113.128:3306/community?characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong
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

## 第二章 社区登录

### 
