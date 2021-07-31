# Spring Bean 基础

- [Spring Bean 基础](#spring-bean-基础)
  - [1. 定义 Spring Bean](#1-定义-spring-bean)
  - [2. BeanDefinition 元信息](#2-beandefinition-元信息)
  - [3. 命名 Spring Bean](#3-命名-spring-bean)
    - [BeanNameGeneratorBean](#beannamegeneratorbean)
  - [4. Spring Bean 的别名](#4-spring-bean-的别名)
  - [5. 注册 Spring Bean](#5-注册-spring-bean)
    - [XML 配置元信息](#xml-配置元信息)
    - [Java 注解配置元信息](#java-注解配置元信息)
      - [@Bean](#bean)
      - [@Component](#component)
      - [@Import](#import)
    - [Java API 配置元信息](#java-api-配置元信息)
  - [6. 实例化 Spring Bean](#6-实例化-spring-bean)
    - [通过构造器](#通过构造器)
    - [通过静态工厂](#通过静态工厂)
    - [通过Bean工厂](#通过bean工厂)
    - [通过FactoryBean](#通过factorybean)
  - [7. 初始化 Spring Bean](#7-初始化-spring-bean)
  - [8. 延迟初始化 Spring Bean](#8-延迟初始化-spring-bean)
  - [9. 销毁 Spring Bean](#9-销毁-spring-bean)
  - [10. 垃圾回收 Spring Bean](#10-垃圾回收-spring-bean)

## 1. 定义 Spring Bean

Spring Bean是通过BeanDefinition进行定义的，BeanDefinition是Spring 中定义Bean的配置元信息的接口。

在BeanDefinition中包含：
* Bean的类名
* Bean 行为配置元素，如作用域、自动绑定的模式，生命周期回调等
* 其他 Bean 引用，又可称作合作者（collaborators）或者依赖（dependencies）
* 配置设置，比如 Bean 属性（Properties）


## 2. BeanDefinition 元信息
BeanDefinition 元信息参考以下列表

|属性（Property ）|说明|
|---|---|
|Class|Bean 全类名，必须是具体类，不能用抽象类或接口|
|Name|Bean 的名称或者 ID|
|Scope|Bean 的作用域（如：singleton、prototype 等）|
|Constructor arguments|Bean 构造器参数（用于依赖注入）|
|Properties|Bean 属性设置（用于依赖注入）|
|Autowiring mode|Bean 自动绑定模式（如：通过名称 byName）|
|Lazy initialization mode|Bean 延迟初始化模式（延迟和非延迟）|
|Initialization method|Bean 初始化回调方法名称|
|Destruction method|Bean 销毁回调方法名称|

在spring中有两种方式构建BeanDefinition：
1. 通过 BeanDefinitionBuilder 构建
2. 通过 AbstractBeanDefinition 以及派生类

下面这段代码，就是通过两种方式构造BeanDefinition，然后通过BeanDefinition设置bean元信息。

```java
    
    // 1.通过 BeanDefinitionBuilder 构建
    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
    //bean 属性依赖注入
    beanDefinitionBuilder.addPropertyValue("id",2).addPropertyValue("name","binbinshan");
    //bean 作用域
    beanDefinitionBuilder.setScope("singleton");
    BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
    
    // 2.通过 AbstractBeanDefinition 以及派生类,以GenericBeanDefinition为例
    GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
    MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
    //add == addPropertyValue
    mutablePropertyValues.add("id",3).add("name","binbinshan");
    //设置bean class
    genericBeanDefinition.setBeanClass(User.class);
    genericBeanDefinition.setScope("singleton");
    genericBeanDefinition.setPropertyValues(mutablePropertyValues);

```


## 3. 命名 Spring Bean

bean 的名称是指，一个 bean 拥有一个或多个标识符（identifiers），这些标识符在 Bean 所在的容器必须是唯一的。通常一个 Bean 仅有一个标识符，如果需要额外的，可考虑使用别名（Alias）来扩充。

另外，Bean 的 id 或 name 属性并非必须制定，如果留空的话，容器会为 Bean 自动生成一个唯一的名称。Bean 的命名尽管没有限制，不过官方建议采用驼峰的方式，更符合 Java 的命名约定。

### BeanNameGeneratorBean 
在Spring中不论**使用xml方式或者注解**的方式，Spring都会通过 BeanNameGeneratorBean （名称生成器）来完成名称的生成。

BeanNameGeneratorBean是接口，有两个实现类DefaultBeanNameGenerator 和 AnnotationBeanNameGenerator 。

展示下DefaultBeanNameGenerator中生成名称的一小段逻辑，也证明了Spring在bean没有定义name时，会帮我们生成bean name：
```java
String generatedBeanName = definition.getBeanClassName();
    //如果没有定义bean name
    if (generatedBeanName == null) {
        //父bean
        if (definition.getParentName() != null) {
            //父bean name不为空，处理逻辑
            generatedBeanName = definition.getParentName() + "$child";
            } else if (definition.getFactoryBeanName() != null) {
                //bean 工厂 name不为空，处理逻辑
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
```


而在AnnotationBeanNameGenerator中的注释上说明了，当使用 @Component, @Repository, @Service, @Controller 就会使用 AnnotationBeanNameGenerator。

一般来说在xml配置中，自定义bean name比较多，而在注解配置，由Spring 生成比较多。

## 4. Spring Bean 的别名

Bean 别名（Alias）的价值

* 复用现有的 BeanDefinition

* 更具有场景化的命名方法，比如： 
```
    <alias name="myApp-dataSource" alias="subsystemA-dataSource"/>
    <alias name="myApp-dataSource" alias="subsystemB-dataSource"/>
```
    



## 5. 注册 Spring Bean

在Spring中如何注册Spring Bean呢？也就是如何注册BeanDefinition 

Spring中提供了三种方式：

1. XML 配置元信息
```
<bean name=”...” ... />
```

2. Java 注解配置元信息
    * @Bean
    * @Component
    * @Import

1. Java API 配置元信息
   * 命名方式：BeanDefinitionRegistry#registerBeanDefinition(String,BeanDefinition)

   * 非命名方式： BeanDefinitionReaderUtils#registerWithGeneratedName(AbstractBeanDefinition,Be anDefinitionRegistry)
   
   * 配置类方式：AnnotatedBeanDefinitionReader#register(Class...)


下面针对这三种方式，以代码为例进行展示：

### XML 配置元信息

xml配置方式很简单：
```
<bean id="user" class="com.spring.ioc.domain.User">
    <property name="id" value="1"/>
    <property name="name" value="binbinshan"/>
</bean>
```

### Java 注解配置元信息

#### @Bean

```
 public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Config.class);
        applicationContext.refresh();
        System.out.println("User 类型的所有 Beans" + applicationContext.getBeansOfType(User.class));

    }
    //定义一个bean
    private static class Config{
        @Bean(name = {"@bean-user"})
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("binbinshan");
            return user;
        }
    }
```

```
输出：User 类型的所有 Beans{@bean-user=User{id=2, name='binbinshan'}}
```

#### @Component
```java
public class AnnotatedBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Config.class);
        applicationContext.refresh();
        System.out.println("User 类型的所有 Beans" + applicationContext.getBeansOfType(User.class));
        System.out.println("Config 类型的所有 Beans" + applicationContext.getBeansOfType(Config.class));

    }

    @Component //通过 @Component 方式,定义当前类作为 Spring Bean（组件）
    public static class Config{
        //@Bean(name = {"@bean-user"})
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("binbinshan");
            return user;
        }
    }
}
```

```
输出：
User 类型的所有 Beans{}
Config 类型的所有 Beans{annotatedBeanDefinitionDemo.Config=com.spring.bean.definitor.AnnotatedBeanDefinitionDemo$Config@43814d18}
```

#### @Import
```java
//通过 @Import 来进行导入
@Import(AnnotatedBeanDefinitionDemo.Config.class)
public class AnnotatedBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Config.class);
        applicationContext.refresh();
        System.out.println("User 类型的所有 Beans" + applicationContext.getBeansOfType(User.class));
        System.out.println("Config 类型的所有 Beans" + applicationContext.getBeansOfType(Config.class));

    }

    //@Component //定义当前类作为 Spring Bean（组件）
    public static class Config{
        //@Bean(name = {"@bean-user"})
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("binbinshan");
            return user;
        }
    }
}
```

```
输出：
User 类型的所有 Beans{}
Config 类型的所有 Beans{annotatedBeanDefinitionDemo.Config=com.spring.bean.definitor.AnnotatedBeanDefinitionDemo$Config@43814d18}
```

### Java API 配置元信息

命名bean注册方式和非命名bean注册方式代码展示：

```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(AnnotationConfigApplicationContext.class);
    applicationContext.refresh();

    // 3.通过 BeanDefinition 注册 API 实现
    // 3.1.命名 Bean 的注册方式
    registerUserBeanDefinition(applicationContext, "binbinshan-user");
    // 3.2. 非命名 Bean 的注册方法
    registerUserBeanDefinition(applicationContext);

    System.out.println("User 类型的所有 Beans" + applicationContext.getBeansOfType(User.class));
    System.out.println("Config 类型的所有 Beans" + applicationContext.getBeansOfType(Config.class));
}

private static void registerUserBeanDefinition(AnnotationConfigApplicationContext applicationContext) {
    registerUserBeanDefinition(applicationContext,null);
}
private static void registerUserBeanDefinition(AnnotationConfigApplicationContext applicationContext, String beanName) {
    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
    beanDefinitionBuilder
            .addPropertyValue("id", 100L)
            .addPropertyValue("name", "binbinshan");

    // 判断如果 beanName 参数存在时
    if (StringUtils.hasText(beanName)) {
        // 注册 BeanDefinition
        applicationContext.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    } else {
        // 非命名 Bean 注册方法
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), applicationContext);
    }
}

```

```
输出：
User 类型的所有 Beans{binbinshan-user=User{id=100, name='binbinshan'}, com.spring.ioc.domain.User#0=User{id=100, name='binbinshan'}}

```


## 6. 实例化 Spring Bean
Bean 实例化（Instantiation） ： bean创建的过程。比如使用构造方法new对象，为对象在内存中分配空间。

* 常规方式
    * 通过构造器（配置元信息：XML、Java 注解和 Java API ）
    * 通过静态工厂（配置元信息：XML 和 Java API ）
    * 通过 Bean 工厂方法（配置元信息：XML和 Java API ）
    * 通过 FactoryBean（配置元信息：XML、Java 注解和 Java API ）

* 特殊方式
    * 通过 ServiceLoaderFactoryBean（配置元信息：XML、Java 注解和 Java API ）
    * 通过 AutowireCapableBeanFactory#createBean(java.lang.Class, int, boolean)
    * 通过 BeanDefinitionRegistry#registerBeanDefinition(String,BeanDefinition)

这里只展示四种常规方式的实例化，采用的是xml配置的方式。统一使用的User类:
```java
@Getter
@Setter
public class User {

    private Long id;

    private String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static User createUser() {
        User user = new User();
        user.setId(100L);
        user.setName("static-method-user");
        return user;
    }
}
```

### 通过构造器
```java
<!-- 通过构造器实例化 -->
<bean id="constructor-arg-user" class="com.spring.ioc.domain.User">
    <constructor-arg index="0" value="100"/>
    <constructor-arg index="1" value="constructor-arg-user"/>
</bean>
```

```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/bean-instantiation-context.xml");
User constructorArgUser = (User) applicationContext.getBean("constructor-arg-user");
```

```java
输出：User{id=100, name='constructor-arg-user'}
```

### 通过静态工厂
```java
<!-- 通过构造器实例化 -->
<!-- 静态工厂实例化Bean -->
<!-- factory-method为指定工厂方法的名字,user类中的静态方法 -->
<bean id="static-method-user" class="com.spring.ioc.domain.User" factory-method="createUser"/>
```

```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/bean-instantiation-context.xml");
User staticMethodUser = (User) applicationContext.getBean("static-method-user");
```

```java
输出：User{id=100, name='static-method-user'}
```


### 通过Bean工厂
```java
<!-- 通过 Bean 工厂方法实例化 -->
<!-- factory-bean 指定工厂类 ，factory-method为指定工厂方法 -->
<bean id="bean-factory-user" factory-bean="userFactory" factory-method="createUser"/>
<bean id="userFactory" class="com.spring.bean.definition.factory.DefaultUserFactory"/>
```

```java
DefaultUserFactory类

public interface UserFactory {
    default User createUser() {
        return User.createUser();
    }
}

public class DefaultUserFactory implements UserFactory{
    @Override
    public User createUser() {
        User user = new User();
        user.setId(100L);
        user.setName("bean-factory-user");
        return user;
    }
}
```


```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/bean-instantiation-context.xml");
User beanFactoryUser = (User) applicationContext.getBean("bean-factory-user");
```

```java
输出：User{id=100, name='bean-factory-user'}
```


### 通过FactoryBean
```java
<!-- 通过 factoryBean 实例化 -->
<bean id="factory-bean-user" class="com.spring.bean.definition.factory.UserFactoryBean"/>
```

```java
//使用FactoryBean，要实现FactoryBean接口，并重写getObject()
public class UserFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        User user = new User();
        user.setId(100L);
        user.setName("factory-bean-user");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
```

```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/bean-instantiation-context.xml");
User factoryBeanUser = (User) applicationContext.getBean("factory-bean-user");
```

```java
输出：User{id=100, name='factory-bean-user'}
```


## 7. 初始化 Spring Bean
Bean 初始化（Initialization）：是为bean的属性赋值的过程
* @PostConstruct 标注方法
* 实现 InitializingBean 接口的 afterPropertiesSet() 方法
* 自定义初始化方法
    * XML 配置：\<bean init-method=”init” ... /\>
    * Java 注解：@Bean(initMethod=”init”)
    * Java API：AbstractBeanDefinition#setInitMethodName(String)

这里我把三种方式放在一起，这样就能看出如果同时使用三种方式进行初始化，三者的加载顺序：

```java
public class BeanInitializationDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类），这里会把BeanInitializationDemo 注入容器，顺带注入userFactory这个bean
        applicationContext.register(BeanInitializationDemo.class);
        // 关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean(initMethod = "initUserFactory")
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }
}
```

```java
public class DefaultUserFactory implements UserFactory, InitializingBean {
    @Override
    public User createUser() {
        User user = new User();
        user.setId(100L);
        user.setName("bean-factory-user");
        return user;
    }
    //@PostConstruct 标注方法
    @PostConstruct
    public void init(){
        System.out.println("@PostConstruct init : UserFactory 初始化");
    }
    
    //实现 InitializingBean 接口的 afterPropertiesSet() 方法
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet init : UserFactory 初始化");
    }
    //自定义初始化方法，使用注解的方式，@Bean(initMethod=”init”)
    public void initUserFactory(){
        System.out.println("initUserFactory init : UserFactory 初始化");
    }
    
}

```

```java
启动容器后，会实例化bean，然后初始化，我们定义了三个初始化的方法，输出如下：

@PostConstruct init : UserFactory 初始化
afterPropertiesSet init : UserFactory 初始化
initUserFactory init : UserFactory 初始化

可以看出先执行了@PostConstruct ，然后是 afterPropertiesSet ，最后是我们自定义的initUserFactory
```


## 8. 延迟初始化 Spring Bean

Bean 延迟初始化（Lazy Initialization）
* XML 配置：\<bean lazy-init=”true” ... /\>
* Java 注解：@Lazy(true)

这里的配置就不多做介绍了，主要关注点在于 **延迟加载的对象和非延迟加载对象初始化的差异**。这里有一段代码,通过改变userFactory是否延迟加载，打印出的日志：

```java
public class BeanInitializationDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(BeanInitializationDemo.class);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 非延迟初始化在 Spring 应用上下文启动完成后，被初始化
        System.out.println("Spring 应用上下文已启动...");
        // 依赖查找 UserFactory
        UserFactory userFactory = (UserFactory) applicationContext.getBean("userFactory");
        System.out.println(userFactory);
        System.out.println("Spring 应用上下文准备关闭...");
        // 关闭 Spring 应用上下文
        applicationContext.close();
        System.out.println("Spring 应用上下文已关闭...");
    }

    @Bean(initMethod = "initUserFactory")
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }
}
```

```java
@PostConstruct init : UserFactory 初始化
afterPropertiesSet init : UserFactory 初始化
initUserFactory init : UserFactory 初始化
Spring 应用上下文已启动...
是否延迟加载 ：false
com.spring.bean.definition.factory.DefaultUserFactory@27808f31
Spring 应用上下文准备关闭...
Spring 应用上下文已关闭...
```

当我把延迟加载加上后，再看打印数据：
```java
@Bean(initMethod = "initUserFactory")
@Lazy
//标注是否延迟加载
public UserFactory userFactory() {
    return new DefaultUserFactory();
}
```
```
Spring 应用上下文已启动...
是否延迟加载 ：true
@PostConstruct init : UserFactory 初始化
afterPropertiesSet init : UserFactory 初始化
initUserFactory init : UserFactory 初始化
com.spring.bean.definition.factory.DefaultUserFactory@32d2fa64
Spring 应用上下文准备关闭...
Spring 应用上下文已关闭...
```

通过上述的输出打印，可以看出**非延迟加载的对象是在应用上下文启动前就完成了初始化。
而延迟加载的对象则是在应用上下文启动后，进行依赖查找的时候才会初始化**。

## 9. 销毁 Spring Bean
Bean 销毁（Destroy）
* @PreDestroy 标注方法
* 实现 DisposableBean 接口的 destroy() 方法
* 自定义销毁方法
    * XML 配置：\<bean destroy=”destroy” ... /\>
    * Java 注解：@Bean(destroy=”destroy”)
    * Java API：AbstractBeanDefinition#setDestroyMethodName(String)

销毁和初始化是相反的动作，我们还是一起输出三种方式，看最后的打印顺序：

```java
//这里构建上下文和初始化是一样的就不重复写了，只写区别的地方。
@Bean(initMethod = "initUserFactory",destroyMethod ="destroyUserFactory")
public UserFactory userFactory() {
    return new DefaultUserFactory();
}
```

```java
public class DefaultUserFactory implements UserFactory, InitializingBean, DisposableBean {

    @PreDestroy
    public void preDestroy(){
        System.out.println("@PreDestroy destroy : UserFactory 销毁");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy : UserFactory 销毁");
    }

    public void destroyUserFactory() throws Exception {
        System.out.println("destroyUserFactory destroy : UserFactory 销毁");
    }
}
```

```java
输出：
@PreDestroy destroy : UserFactory 销毁
DisposableBean destroy : UserFactory 销毁
destroyUserFactory destroy : UserFactory 销毁
```

可以得出结论先执行了 @PreDestroy ，然后是接口 DisposableBean ，最后是自定义销毁destroyMethod。

所以最后结论，初始化和销毁三种方式的执行顺序：
1. 都是先执行 javax.annotation 实现的注解
2. 然后是实现Spring接口，调用方法
3. 最后才是自定义初始化方法和自定义销毁方法。




## 10. 垃圾回收 Spring Bean
Bean 垃圾回收（GC）
1. 关闭 Spring 容器（应用上下文）
2. 执行 GC
3. Spring Bean 覆盖的 finalize() 方法被回调

Spring的bean要被垃圾回收，必须先关闭应用上下文，执行GC的步骤不是必须的。当然 finalize()也不是一定会被调用。
