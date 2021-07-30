# Spring IoC 容器概述

- [Spring IoC 容器概述](#spring-ioc-容器概述)
  - [Spring IoC 依赖查找](#spring-ioc-依赖查找)
    - [根据 Bean 名称查找](#根据-bean-名称查找)
    - [根据 Bean 类型查找](#根据-bean-类型查找)
    - [根据 Java 注解查找](#根据-java-注解查找)
  - [Spring IoC 依赖注入](#spring-ioc-依赖注入)
    - [根据 Bean 名称注入单个对象](#根据-bean-名称注入单个对象)
    - [根据 Bean 名称注入集合对象](#根据-bean-名称注入集合对象)
    - [根据 Bean 类型注入](#根据-bean-类型注入)
    - [注入容器內建 Bean 对象](#注入容器內建-bean-对象)
    - [注入非 Bean 对象](#注入非-bean-对象)
  - [Spring Ioc的依赖来源](#spring-ioc的依赖来源)
  - [Spring Ioc的元信息配置](#spring-ioc的元信息配置)
  - [Spring IoC容器底层](#spring-ioc容器底层)
  - [Spring 应用上下文](#spring-应用上下文)

## Spring IoC 依赖查找

Spring IOC依赖查找有以下几种方式：

1. 根据 Bean 名称查找
    * 实时查找
    * 延迟查找
2. 根据 Bean 类型查找
    * 单个 Bean 对象
    * 集合 Bean 对象
3. 根据 Bean 名称 + 类型查找
4. 根据 Java 注解查找
    * 单个 Bean 对象
    * 集合 Bean 对象


下面用代码展示几种典型的依赖查找

### 根据 Bean 名称查找

1. 自定义bean
    ```java
    <bean id="user" class="com.spring.ioc.domain.User">
        <property name="id" value="1"/>
        <property name="name" value="binbinshan"/>
    </bean>
    ```
    
2. 根据bean name查找

    ```java
    private static void lookupByName(BeanFactory beanFactory){
        User user = (User) beanFactory.getBean("user");
        System.out.println("根据Bean name 查找：" + user);
    }
    
    输出：
    根据Bean name 查找：User{id=1, name='binbinshan'}
    ```


### 根据 Bean 类型查找
1. 自定义bean,一个user,一个继承自user的super user
    ```java
    <bean id="user" class="com.spring.ioc.domain.User">
        <property name="id" value="1"/>
        <property name="name" value="binbinshan"/>
    </bean>
    <!--继承自user -->
    <bean id="super" class="com.spring.ioc.domain.SuperUser" parent="user" primary="true">
        <property name="address" value="beijing"/>
    </bean>
    
    ```
    
2. 根据bean type查找
    ```java
    private static void lookupByType(BeanFactory beanFactory){
      User bean = beanFactory.getBean(User.class);
      System.out.println("根据Bean type 查找：" + bean);
    }
    
    输出：
   根据Bean type 查找：SuperUser{address='beijing'} User{id=1, name='binbinshan'}

    ```

### 根据 Java 注解查找
1. 自定义注解
    ```
    //自定义注解
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SuperUserAnnotation {
    }
    ```
    
2. 定义superUser

    ```
    @Getter
    @Setter
    @SuperUserAnnotation //使用自定义注解
    public class SuperUser extends User {
        private String address;
    }
    ```

1. 自定义bean
    ```
    <bean id="super" class="com.spring.ioc.domain.SuperUser" parent="user" primary="true">
        <property name="address" value="beijing"/>
    </bean>
    ```

1. 根据Java注解查找
    ```
    private static void lookupByAnnotation(BeanFactory beanFactory){

        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = (Map) listableBeanFactory.getBeansWithAnnotation(SuperUserAnnotation.class);
            System.out.println("根据注解查找 SuperUser 集合对象：" + users);
        }
    }
    
    输出：
    根据注解查找 SuperUser 集合对象：{super=SuperUser{address='beijing'} User{id=1, name='binbinshan'}}
    ```
    
## Spring IoC 依赖注入

Spring IOC依赖注入有以下几种方式：

1. 根据 Bean 名称注入
2. 根据 Bean 类型注入
    * 单个 Bean 对象
    * 集合 Bean 对象
1. 注入容器內建 Bean 对象
2. 注入非 Bean 对象
3. 注入类型
    * 实时注入
    * 延迟注入

### 根据 Bean 名称注入单个对象

1. userRepository

    ```java
    @Getter
    @Setter
    public class UserRepository {
        /**
         * 自定义单个Bean
         */
        private User user;
    }
    ```
    
1. 自定义userRepostitory bean

    ```java
    <import resource="dependency-lookup-context.xml"/>
    //autowire指定通过name注入bean,也就是注入user
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byName"/>
    
    ```
1. 查询
    ```java
    UserRepository userRepository = beanFactory.getBean(UserRepository.class);
    User user = userRepository.getUser();
    System.out.println(user);

    
    输出 ：
    User{id=1, name='binbinshan'}
    ```

### 根据 Bean 名称注入集合对象
1. userRepository

    ```java
    @Getter
    @Setter
    public class UserRepository {
        /**
         * 自定义集合 Bean
         */
        private List<User> users;
    }
    ```
    
1. 自定义userRepostitory bean

    ```java
    <import resource="dependency-lookup-context.xml"/>
    //autowire指定通过name注入bean
    //在参数中指定users的集合需要注入的bean。
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byName">
        <property name="users">
            <util:list>
                <ref bean="super" />
                <ref bean="user" />
            </util:list>
        </property>
    </bean>
    
    ```
1. 查询
    ```java
    List<User> users = userRepository.getUsers();
    users.stream().forEach(c -> System.out.println(c.toString()));
    
    
    输出 ：
    SuperUser{address='beijing'} User{id=1, name='binbinshan'}
    User{id=1, name='binbinshan'}
    ```

### 根据 Bean 类型注入
1. userRepository

    ```java
    @Getter
    @Setter
    public class UserRepository {
        private User user;
    }
    ```
    
1. 自定义userRepostitory bean

    ```java
    <import resource="dependency-lookup-context.xml"/>
    //autowire指定通过byType注入bean
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byType"/>
        
    ```
1. 查询
    ```java
    User user = userRepository.getUser();
    System.out.println(user);
    
    输出 ：
    User{id=1, name='binbinshan'}
    ```


### 注入容器內建 Bean 对象
    
1. 自定义userRepostitory bean

    ```java
    <import resource="dependency-lookup-context.xml"/>
    //autowire指定通过byType注入bean 通过byName是找不到beanFactory 和 objectFactory
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byType"/>
        
    ```
1. 查询
    ```java
    Environment environment = applicationContext.getBean(Environment.class);
    System.out.println("获取 Environment 类型的 Bean：" + environment);    
    输出 ：
    获取 Environment 类型的 Bean：StandardEnvironment {activeProfiles=[], defaultProfiles=[default], propertySources=[PropertiesPropertySource {name='systemProperties'}, SystemEnvironmentPropertySource {name='systemEnvironment'}]}
    ```

### 注入非 Bean 对象
1. userRepository

    ```java
    @Getter
    @Setter
    public class UserRepository {
        private BeanFactory beanFactory;
        private ObjectFactory<ApplicationContext> objectFactory;
    }
    ```
    
1. 自定义userRepostitory bean

    ```java
    <import resource="dependency-lookup-context.xml"/>
    //autowire指定通过byType注入bean 通过byName是找不到beanFactory 和 objectFactory
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byType"/>
        
    ```
1. 查询
    ```java
    BeanFactory beanFactory = userRepository.getBeanFactory();
    System.out.println(beanFactory);
    
    ObjectFactory objectFactory = userRepository.getObjectFactory();
    System.out.println(objectFactory);
    
    输出 ：
    org.springframework.beans.factory.support.DefaultListableBeanFactory@5f2050f6: defining beans [user,userRepository]; root of factory hierarchy
    org.springframework.beans.factory.support.DefaultListableBeanFactory$DependencyObjectProvider@a7e666
    ```

    为什么说beanFactory 和 objectFactory 不是bean呢？

    因为通过beanFactory.getBean(BeanFactory.class)获取bean的时候会抛出NoSuchBeanDefinitionException 说明BeanFactory不是bean。



## Spring Ioc的依赖来源

通过上面的依赖注入和依赖查找，我们可以得出Spring Ioc的依赖来源有三种：

1. 自定义 Bean ： 我们自己定义的bean，交给spring ioc 管理

2. 容器內建 Bean 对象 ：Environment...

3. 容器內建依赖 ：beanFactory 和 objectFactory


## Spring Ioc的元信息配置

Spring IOC中的配置，一般分为三类：

1. Bean 定义配置
    * 基于 XML 文件
    * 基于 Properties 文件
    * 基于 Java 注解
    * 基于 Java API

2. IoC 容器自身配置
    * 基于 XML 文件
    * 基于 Java 注解
    * 基于 Java API

1. 外部化属性配置
    * 基于 Java 注解

    
## Spring IoC容器底层

在Spring中 BeanFactory 和 ApplicationContext 都可以通过getBean获取Bean，那么这两者谁才是Spring IOC容器的底层实现呢？

先看个代码：

1. 定义userRepository里面有一个BeanFactory对象

    ```java
    @Getter
    @Setter
    public class UserRepository {
        private BeanFactory beanFactory;
    }
    ```
    
1. 自定义userRepostitory bean

    ```java
    //autowire指定通过byType注入bean 通过byName是找不到beanFactory
    <bean id="userRepository" class="com.spring.ioc.repository.UserRepository" autowire="byType"/>
        
    ```
1. 查询
    ```java
    BeanFactory beanFactory= new ClassPathXmlApplicationContext("META-INF/dependency-injection-context.xml");
    UserRepository userRepository = beanFactory.getBean(UserRepository.class);
    System.out.println(userRepository.getBeanFactory() == beanFactory);
    
    输出 ： false
    
    ```

通过上面的代码可以看出，两个BeanFactory不是一个对象。

如果修改下查询代码：

```java
    ApplicationContext applicationContext= new ClassPathXmlApplicationContext("META-INF/dependency-injection-context.xml");
    UserRepository userRepository = applicationContext.getBean(UserRepository.class);
    System.out.println(userRepository.getBeanFactory() == applicationContext);

    输出: false
```
结果还是false，并且对象的类型可以是ApplicationContext和BeanFactory，说明ClassPathXmlApplicationContext是继承BeanFactory。

那么为什么ApplicationContext 和 BeanFactory 不一样呢？通过底层代码可以得到：
以ClassPathXmlApplicationContext为例

* ClassPathXmlApplicationContext 继承 AbstractXmlApplicationContext

* AbstractXmlApplicationContext 继承 AbstractRefreshableConfigApplicationContext

* AbstractRefreshableConfigApplicationContext 继承 AbstractRefreshableApplicationContext

AbstractRefreshableApplicationContext中有一个抽象方法实现**getBeanFactory**()，
获取的是**DefaultListableBeanFactory**这个对象。这样也是为什么打印userRepository.getBeanFactory()对象，输出的结果是DefaultListableBeanFactory

DefaultListableBeanFactory 这个对象是以一个组合的方式在AbstractRefreshableApplicationContext 中，类似与代理的方式，ClassPathXmlApplicationContext进行getBean的时候，就是使用BeanFactory.getBean进行获取的。

这也说明了BeanFactory是IOC底层的实现，这也是为什么ApplicationContext和BeanFactory不一样的原因，因为两个不是一个对象，而是组合的方式在ClassPathXmlApplicationContext中。

## Spring 应用上下文
为什么有了BeanFactory，还需要ApplicationContext呢？

ApplicationContext 是 BeanFactory 的子接口。BeanFactory提供了IOC配置框架和基本功能，而ApplicationContext添加了更多企业特定的功能。ApplicationContext是对一个BeanFactory完整的超集。

例如ApplicationContext 除了 IoC 容器角色，还有提供：
* 面向切面（AOP）
* 配置元信息（Configuration Metadata）
* 资源管理（Resources）
* 事件（Events）
* 国际化（i18n）
* 注解（Annotations）
* Environment 抽象（Environment Abstraction）




