# 牛客论坛

## Spring
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



