# Spring 依赖注入

- [Spring 依赖注入](#spring-依赖注入)
  - [依赖注入模式](#依赖注入模式)
  - [依赖注入类型](#依赖注入类型)
  - [自动绑定](#自动绑定)
    - [自动绑定（Autowiring）模式](#自动绑定autowiring模式)
    - [自动绑定（Autowiring）限制和不足](#自动绑定autowiring限制和不足)
  - [Setter 方法注入](#setter-方法注入)
    - [手动模式](#手动模式)
      - [XML 资源配置元信息](#xml-资源配置元信息)
      - [Java 注解配置元信息](#java-注解配置元信息)
      - [API 配置元信息](#api-配置元信息)
    - [自动模式](#自动模式)
      - [byName](#byname)
      - [byType](#bytype)
  - [构造器注入](#构造器注入)
    - [手动模式](#手动模式-1)
      - [XML 资源配置元信息](#xml-资源配置元信息-1)
      - [Java 注解配置元信息](#java-注解配置元信息-1)
      - [API 配置元信息](#api-配置元信息-1)
    - [自动模式](#自动模式-1)
  - [字段注入](#字段注入)
  - [方法注入](#方法注入)
  - [接口回调注入](#接口回调注入)
  - [依赖注入类型选择](#依赖注入类型选择)
    - [少依赖：构造器注入](#少依赖构造器注入)
    - [多依赖：Setter 方法注入](#多依赖setter-方法注入)
    - [便利性：字段注入](#便利性字段注入)
    - [声明类：方法注入](#声明类方法注入)
  - [基础类型注入](#基础类型注入)
  - [集合类型注入](#集合类型注入)
  - [限定注入](#限定注入)
  - [延迟依赖注入](#延迟依赖注入)
  - [@Autowired 注入原理](#autowired-注入原理)
  - [Java通用注解注入原理](#java通用注解注入原理)
  - [自定义依赖注入注解](#自定义依赖注入注解)

## 依赖注入模式
依赖注入的模式：

* 手动模式 - 配置或者编程的方式，提前安排注入规则

    * XML 资源配置元信息
    
    * Java 注解配置元信息
    
    * API 配置元信息

* 自动模式 - 实现方提供依赖自动关联的方式，按照內建的注入规则
    * Autowiring（自动绑定）


## 依赖注入类型


|依赖注入类型|配置元数据举例|
|---|---|
|Setter 方法|\<proeprty name="user" ref="userBean"/\> |
|构造器|\<constructor-arg name="user" ref="userBean" /\>|
|字段|@Autowired User user;|
|方法|@Autowired public void user(User user) { ... }|
|接口回调|class MyBean implements BeanFactoryAware { ... }|


## 自动绑定

autowire 是指Spring容器可以自动装配协作bean之间的关系。自动绑定可以有效减少指定属性或构造函数参数。

### 自动绑定（Autowiring）模式

枚举实现：org.springframework.beans.factory.annotation.Autowire

|模式|说明|
|---|---|
|no|默认值，未激活 Autowiring，需要手动指定依赖注入对象。|
|byName|根据被注入属性的名称作为 Bean 名称进行依赖查找，并将对象设置到该属性。|
|byType|根据被注入属性的类型作为依赖类型进行查找，并将对象设置到该属性。|
|constructor|特殊 byType 类型，用于构造器参数。|

### 自动绑定（Autowiring）限制和不足
1. 不能自动绑定简单属性，例如 Strings、 和Classes（以及此类简单属性的数组）。
2. 自动装配不如显式装配精确。容易把人搞晕。
3. 容器内的多个 bean 定义可能与要自动装配的 setter 方法或构造函数参数指定的类型相匹配。如果没有唯一的 bean 定义可用，则抛出异常。 
 
## Setter 方法注入

* 手动模式
    * XML 资源配置元信息
    * Java 注解配置元信息
    * API 配置元信息

* 自动模式
    * byName
    * byType


### 手动模式
手动模式是指显示的去配置Bean的定义和依赖。下面以代码为例展示：

#### XML 资源配置元信息

* 定义userHolder

```java
public class UserHolder {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }
}
```

* 定义xml配置

```java
<bean id="userHolder" class="spring.dependency.injection.UserHolder">
    <property name="user" ref="user"/>
</bean>

<bean id="user" class="com.spring.ioc.domain.User">
    <property name="id" value="1"/>
    <property name="name" value="binbinshan"/>
</bean>
```

* 测试代码

```java
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
String xmlResourcePath = "classpath:/META-INF/dependency-setter-injection-context.xml";
// 加载 XML 资源，解析并且生成 BeanDefinition
beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
// 依赖查找并且创建 Bean
UserHolder userHolder = beanFactory.getBean(UserHolder.class);
System.out.println(userHolder);
```

输出：UserHolder{user=User{id=1, name='binbinshan'}}

#### Java 注解配置元信息

userHolder定义同xml代码中一致

* 使用xml定义user Bean

```java
<bean id="user" class="com.spring.ioc.domain.User">
    <property name="id" value="1"/>
    <property name="name" value="binbinshan"/>
</bean>
```

* 使用注解+xml方式

```java
public class AnnotationDependencySetterInjectionDemo {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencySetterInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/xml-dependency-setter-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);

        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        UserHolder userHolder = new UserHolder();
        userHolder.setUser(user);
        return userHolder;
    }
}
```
输出：UserHolder{user=User{id=1, name='binbinshan'}}


#### API 配置元信息

1. 构建 UserHolder 的 BeanDefinition
2. 注册 UserHolder 的 BeanDefinition
3. 读取 xml 配置中的 User(同上，这里就不展示代码了)
4. 启动 Spring 应用上下文
5. 使用 Bean
6. 关闭 Spring 应用上下文

```java
public class ApiDependencySetterInjectionDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 生成 UserHolder 的 BeanDefinition
        BeanDefinition userHolderBeanDefinition = createUserHolderBeanDefinition();
        // 注册 UserHolder 的 BeanDefinition
        applicationContext.registerBeanDefinition("userHolder", userHolderBeanDefinition);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);

        String xmlResourcePath = "classpath:/META-INF/api-dependency-setter-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    /**
     * 为 {@link UserHolder} 生成 {@link BeanDefinition}
     *
     * @return
     */
    private static BeanDefinition createUserHolderBeanDefinition() {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);
        definitionBuilder.addPropertyReference("user", "user");
        return definitionBuilder.getBeanDefinition();
    }
}
```

输出：UserHolder{user=User{id=1, name='binbinshan'}}

 
### 自动模式
自动模式一般是在XML配置中使用

#### byName

* 定义xml配置,会根据autowire="byName" 自动注入setter方法对应参数

```java
    <import resource="classpath:/META-INF/dependency-lookup-context.xml"/>
    <bean id="userHolder" class="spring.dependency.injection.UserHolder" autowire="byName"/>
```

* 测试

```java
public class AutoWiringByNameDependencySetterInjectionDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        String xmlResourcePath = "classpath:/META-INF/autowiring-dependency-setter-injection.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 依赖查找并且创建 Bean
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder);
    }
}
```

输出：UserHolder{User{id=1, name='binbinshan'}}

#### byType

因为我们在dependency-lookup-context.xml 中定义了两个user类型的对象，一个是user，一个是superUser，所以使用byType时需要设置一个主类（使用Primary），这里设置superUser为Primary。

```java
<bean id="user" class="com.spring.ioc.domain.User">
    <property name="id" value="1"/>
    <property name="name" value="binbinshan"/>
</bean>

<bean id="super" class="com.spring.ioc.domain.SuperUser" parent="user" primary="true">
    <property name="address" value="beijing"/>
</bean>

```
 
 
* xml 配置

```java
<bean id="userHolder" class="spring.dependency.injection.UserHolder" autowire="byType"/>

```

* 测试

```java
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        String xmlResourcePath = "classpath:/META-INF/autowiring-dependency-setter-injection.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 依赖查找并且创建 Bean
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder);
    }
```
 
 输出：UserHolder{user=SuperUser{address='beijing'} User{id=1, name='binbinshan'}}


## 构造器注入

构造器注入是指使用构造方法注入，spring官网推荐使用构造器注入。

* 手动模式
    * XML 资源配置元信息
    * Java 注解配置元信息
    * API 配置元信息

* 自动模式
    * constructor
 
 
### 手动模式
总体代码与setter类似，区别在于使用构造注入

#### XML 资源配置元信息

* 修改UserHolder 添加构造方法

```java
public class UserHolder {

    private User user;

    public UserHolder() {}
    public UserHolder(User user) {
        this.user = user;
    }
}
```

* 定义xml,注意使用了constructor-arg

```java
<bean id="userHolder" class="spring.dependency.injection.UserHolder">
    <constructor-arg name="user" ref="user"/>
</bean>

<bean id="user" class="com.spring.ioc.domain.User">
    <property name="id" value="1"/>
    <property name="name" value="binbinshan"/>
</bean>
```

* 测试

```java
public static void main(String[] args) {

    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    String xmlResourcePath = "classpath:/META-INF/xml-dependency-constructor-injection-context.xml";
    // 加载 XML 资源，解析并且生成 BeanDefinition
    beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
    // 依赖查找并且创建 Bean
    UserHolder userHolder = beanFactory.getBean(UserHolder.class);
    System.out.println(userHolder);
}
```

输出：UserHolder{user=User{id=1, name='binbinshan'}}

#### Java 注解配置元信息

* 注册当前类为配置类，加载 UserHolder Bean ，使用构造方法注入的UserHolder。

```java
 public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencyConstructorInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/annotation-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);

        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        return new UserHolder(user);
    }
```
输出：UserHolder{user=User{id=1, name='binbinshan'}}

#### API 配置元信息

使用 API方法definitionBuilder.addConstructorArgReference，完成注入。

```java
public class ApiDependencyConstructorInjectionDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 生成 UserHolder 的 BeanDefinition
        BeanDefinition userHolderBeanDefinition = createUserHolderBeanDefinition();
        // 注册 UserHolder 的 BeanDefinition
        applicationContext.registerBeanDefinition("userHolder", userHolderBeanDefinition);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);

        String xmlResourcePath = "classpath:/META-INF/api-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    /**
     * 为 {@link UserHolder} 生成 {@link BeanDefinition}
     *
     * @return
     */
    private static BeanDefinition createUserHolderBeanDefinition() {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);
        definitionBuilder.addConstructorArgReference("user");
        return definitionBuilder.getBeanDefinition();
    }
}
```

输出：UserHolder{user=User{id=1, name='binbinshan'}}

 
### 自动模式
使用constructor完成自动注入，类似于byType的特殊类型。


* 定义xml，这里需要注意，constructor是类似于byType的，所有现在有两个User类型，superUser 和 user,由于 superUser bean 指定了primary。所以构造注入的也是 superUser。

```java
<import resource="classpath:/META-INF/dependency-lookup-context.xml"/>

<bean id="userHolder" class="spring.dependency.injection.UserHolder" autowire="constructor"/>

```

* 测试

```java
public static void main(String[] args) {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

    String xmlResourcePath = "classpath:/META-INF/autowiring-dependency-construuctor-injection.xml";
    // 加载 XML 资源，解析并且生成 BeanDefinition
    beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
    // 依赖查找并且创建 Bean
    UserHolder userHolder = beanFactory.getBean(UserHolder.class);
    System.out.println(userHolder);
}
```

输出：UserHolder{user=SuperUser{address='beijing'} User{id=1, name='binbinshan'}}


## 字段注入
字段注入只有手动模式。

Java 注解配置元信息
1. @Autowired
2. @Resource
3. @Inject（可选）


使用 @Autowired 和 @Resource 注入：

```java
public class AnnotationDependencyFieldInjectionDemo {

    @Autowired
    private UserHolder userHolder;
    @Resource
    private UserHolder userHolder2;

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencyFieldInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/annotation-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        AnnotationDependencyFieldInjectionDemo demo = applicationContext.getBean(AnnotationDependencyFieldInjectionDemo.class);
        // @Autowired 字段关联
        UserHolder userHolder = demo.userHolder;
        System.out.println(userHolder);
        System.out.println(demo.userHolder2);
        System.out.println(userHolder == demo.userHolder2);

        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        return new UserHolder(user);
    }
}
```

这里不是直接获取UserHolder Bean，而是通过获取到 配置类（当前类），然后通过获取字段的形式进行获取Bean，可以发现的是通过 @Autowired 和 @Resource 注入的两个对象是一样的，这是因为 @Bean 注入userHolder 默认是单例的。

## 方法注入

方法注入只存在手动模式。

Java 注解配置元信息

* @Autowired
* @Resource
* @Inject（可选）
* @Bean

使用 @Autowired 和 @ Resource 和 @Bean注入

```java
public class AnnotationDependencyMethodInjectionDemo {

    private UserHolder userHolder;
    private UserHolder userHolder2;

    @Autowired
    public void init1(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @Resource
    public void init2(UserHolder userHolder2) {
        this.userHolder2 = userHolder2;
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencyMethodInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/annotation-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        AnnotationDependencyMethodInjectionDemo demo = applicationContext.getBean(AnnotationDependencyMethodInjectionDemo.class);
        // @Autowired 字段关联
        UserHolder userHolder = demo.userHolder;
        System.out.println(userHolder);
        System.out.println(demo.userHolder2);
        System.out.println(userHolder == demo.userHolder2);

        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        return new UserHolder(user);
    }
}
```

可以发现方法注入的代码 和 字段注入代码 很相似，区别的地方的是我们通过init1() 和 init2() 方法注入 userHolder 对象，然后分别给 userHolder 和 userHolder2 字段赋值，并且也是用 @Bean 注入 user。 其余代码与字段注入代码一致。

## 接口回调注入

Spring提供了广泛的Aware回调接口，让bean向容器表明它们需要某种基础设施依赖。

假设一个bean想要使用 ApplicationContext 对象和 BeanFactory，来查看容器中有哪些bean，代码如下：

```java
//实现了 BeanFactoryAware, ApplicationContextAware 接口
public class AwareInterfaceDependencyInjectionDemo implements BeanFactoryAware, ApplicationContextAware {
    //用一个全局变量，保存接口回调时传入的beanFactory对象
    private static BeanFactory beanFactory;
    //用一个全局变量，保存接口回调时传入的applicationContext对象
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类） -> Spring Bean
        context.register(AwareInterfaceDependencyInjectionDemo.class);
        // 启动 Spring 应用上下文
        context.refresh();
        System.out.println(beanFactory == context.getBeanFactory());
        System.out.println(applicationContext == context);
        // 显示地关闭 Spring 应用上下文
        context.close();
    }

    //接口实现方法，设置BeanFactory
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        AwareInterfaceDependencyInjectionDemo.beanFactory = beanFactory;
    }

    //接口实现方法，设置ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AwareInterfaceDependencyInjectionDemo.applicationContext = applicationContext;
    }
}
```

在Spring中提供了Aware 系列接口回调，如下：


|內建接口|说明|
|---|---|
|BeanFactoryAware|获取 IoC 容器 - BeanFactory|
|ApplicationContextAware|获取 Spring 应用上下文 - ApplicationContext 对象|
|EnvironmentAware|获取 Environment 对象|
|ResourceLoaderAware|获取资源加载器 对象 - ResourceLoader|
|BeanClassLoaderAware|获取加载当前 Bean Class 的 ClassLoader|
|BeanNameAware|获取当前 Bean 的名称|
|MessageSourceAware|获取 MessageSource 对象，用于 Spring 国际化|
|ApplicationEventPublisherAware|获取 ApplicationEventPublishAware 对象，用于 Spring 事件|
|EmbeddedValueResolverAware|获取 StringValueResolver 对象，用于占位符处理|


## 依赖注入类型选择


### 少依赖：构造器注入

在Spring官网中关于 setter注入和构造器注入是主张使用构造器注入。

这是因为构造器注入是强制注入，如果少依赖注入(构造器参数不超过三个)，建议使用构造器。

如果三个构造参数以上以上建议使用setter注入。

### 多依赖：Setter 方法注入

在多依赖项的情况下，建议使用setter注入。

但是setter注入时，它注入的时机的先后顺序完全依赖用户的操作，比如说依赖项中有前后顺序的依赖要求，就必须要特别注意。

### 便利性：字段注入

直接标记在字段上标注 @Autowired 和 @ Resource，使用非常便利，但是在 Spring中已经慢慢淘汰，所以不建议使用。

### 声明类：方法注入
直接在方法上标记@Autowired、@Resource、@Bean 

```java

private UserHolder var1;

@Autowired
public void init1(UserHolder userHolder) {
    this.var1 = userHolder;
}
```

使用@Autowired标记方法，然后通过参数注入 userHolder Bean。




-----------上述讨论的是具体的依赖注入方式，下面针对具体的依赖的类型进行介绍--------


## 基础类型注入

基础类型包括以下几种类型：

* 原生类型（Primitive）：boolean、byte、char、short、int、float、long、double

* 标量类型（Scalar）：Number、Character、Boolean、Enum、Locale、Charset、Currency、Properties、UUID

* 常规类型（General）：Object、String、TimeZone、Calendar、Optional 等

* Spring 类型：Resource、InputSource、Formatter 等

常见的原生类型，这里就不展示代码，这里使用代码说明下 标量类型中的Enum 和 Spring类型中的Resource。


* 定义User

```java
//需要set方法，因为通过setter注入的。
public class User {
    private Long id;
    //原生类型
    private String name;
    //spring 类型 的 Resource
    private Resource localResource;
    //枚举类型
    private City city;
    //数组类型
    private City[] birthCity;
    //集合类型
    private List<City> lifeCity;
    //集合类型
    private List<City> workCity;
}
```

* 定义 City Enum

```java
public enum City {
    BEIJING,
    SHANGHAI,
    GUANGZHOU
}
```


* 定义User Bean

```java
    <bean id="user" class="com.spring.ioc.domain.User">
        <property name="id" value="1"/>
        <property name="name" value="binbinshan"/>
        <property name="city" value="BEIJING"/>
        <property name="localResource" value="classpath:META-INF/user-config.properties"/>
    </bean>

```

* 测试

```java
    public static void main(String[] args) {

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String xmlResourcePath = "classpath:/META-INF/xml-dependency-setter-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 依赖查找并且创建 Bean
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder);
    }
```

输出：UserHolder{user=User{id=1, name='binbinshan', localResource=class path resource [META-INF/user-config.properties], city=BEIJING}

## 集合类型注入

集合类型包括以下几种类型：

* 数组类型（Array）：原生类型、标量类型、常规类型、Spring 类型

* 集合类型（Collection）
    * Collection：List、SetSortedSet、NavigableSet、EnumSet）
    * Map：Properties


这里展示数组类型和集合类型list，元素类型为枚举，

* 定义user，同上基本类型中的User

* 定义userBean，这里使用注入List类型数据时，采用2种方法

```java
    <bean id="user" class="com.spring.ioc.domain.User">
        <property name="id" value="1"/>
        <property name="name" value="binbinshan"/>
        <property name="city" value="BEIJING"/>
        <property name="localResource" value="classpath:META-INF/user-config.properties"/>
        <property name="birthCity" value="BEIJING"/>
        <!-- List 方式一-->
        <property name="lifeCity" value="BEIJING,SHANGHAI"/>
        <!-- List 方式二-->
        <property name="workCity">
            <list>
                <value>SHANGHAI</value>
                <value>GUANGZHOU</value>
            </list>
        </property>
    </bean>
```

* 测试

```java
    public static void main(String[] args) {

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String xmlResourcePath = "classpath:/META-INF/xml-dependency-setter-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 依赖查找并且创建 Bean
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder);
    }
```

输出：UserHolder{user=User{id=1, name='binbinshan', localResource=class path resource [META-INF/user-config.properties], city=BEIJING, birthCity=[BEIJING], lifeCity=[BEIJING, SHANGHAI], workCity=[SHANGHAI, GUANGZHOU]}}


## 限定注入

限定注入@Qualifier，主要作用有两个：

1. 在使用@Autowire自动注入的时候，加上@Qualifier(“test”)可以指定注入哪个对象；
2. 可以作为筛选的限定符，我们在做自定义注解时可以在其定义上增加@Qualifier，用来筛选需要的对象。

从使用@Qualifier注解和基于注解 @Qualifier 扩展限定两个方法来介绍：

* 使用注解 @Qualifier 限定

    * 通过 Bean 名称限定

    * 通过分组限定

* 基于注解 @Qualifier 扩展限定

    * 自定义注解 - 如 Spring Cloud @LoadBalanced


在下列代码中，定义6个user bean，来展示 通过 Bean 名称限定、通过分组限定和基于注解 @Qualifier 扩展限定。


```java
public class QualifierAnnotationDependencyInjectionDemo {

    /**
     * user bean  ->  primary = true
     */
    @Autowired
    private User user;

    /**
     * 根据指定 Bean 名称或ID 注入
     */
    @Autowired
    @Qualifier("superUser")
    private User nameUser;

    /**
     * 整体应用上下文存在 6 个 User 类型的 Bean
     * user
     * superUser
     * user1 -> @Qualifier
     * user2 -> @Qualifier
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    private List<User> allUsers;

    /**
     * 2 个 Qualifier user Bean + 2 个 UserGroup user Bean
     * user1 -> @Qualifier
     * user2 -> @Qualifier
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    @Qualifier
    private Collection<User> qualifiedUsers;

    /**
     * 基于 Qualifier 扩展
     * UserGroup 的 2个Bean
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    @UserGroup
    private Collection<User> groupedUsers;

    /**
     * 注入user bean
     * Primary = true
     */
    @Bean
    @Primary
    public User user() {
        return createUser(10L);
    }

    /**
     * 注入superUser bean
     */
    @Bean
    public User superUser() {
        return createUser(100L);
    }

    /**
     * 进行逻辑分组 ，注入 user1 bean
     * primary = true
     */
    @Bean
    @Qualifier
    public User user1() {
        return createUser(1L);
    }

    /**
     * 进行逻辑分组 ，注入 user2 bean
     */
    @Bean
    @Qualifier
    public User user2() {
        return createUser(2L);
    }

    /**
     * 基于注解 @Qualifier 扩展限定，进行逻辑分组 ，注入 user3 bean
     */
    @Bean
    @UserGroup
    public  User user3() {
        return createUser(3L);
    }

    /**
     * 基于注解 @Qualifier 扩展限定，进行逻辑分组 ，注入 user4 bean
     */
    @Bean
    @UserGroup
    public  User user4() {
        return createUser(4L);
    }

    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(QualifierAnnotationDependencyInjectionDemo.class);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        QualifierAnnotationDependencyInjectionDemo demo = applicationContext.getBean(QualifierAnnotationDependencyInjectionDemo.class);

        System.out.println("user ：--------->" + demo.user);
        System.out.println("nameUser ：--------->" + demo.nameUser);
        System.out.println("allUsers ：--------->" + demo.allUsers);
        System.out.println("qualifiedUsers ：--------->" + demo.qualifiedUsers);
        System.out.println("groupedUsers ： --------->" + demo.groupedUsers);

    }
}
```

输出：

```java
user ：--------->User{id=10, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
nameUser ：--------->User{id=100, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
allUsers ：--------->[User{id=10, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=100, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=1, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=2, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=3, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=4, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}]
qualifiedUsers ：--------->[User{id=1, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=2, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=3, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=4, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}]
groupedUsers ： --------->[User{id=3, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, User{id=4, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}]
```

其中@UserGroup是基础@Qualifier进行了扩展。结构如下：

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
public @interface UserGroup {
}
```

## 延迟依赖注入

**延迟依赖注入和延迟加载**是不一样的，可以通过@Lazy来延迟加载Bean。

@Lazy：容器一般都会在启动的时候实例化所有单实例 bean 。如果我们想要 Spring 在启动的时候延迟加载 bean，即在调用某个 bean 的时候再去初始化，那么就可以使用 @Lazy 注解。

这里介绍的是延迟依赖注入，需要注意区分，介绍两种延迟注入的方式。

1. 使用 API ObjectFactory 延迟注入

2. 使用 API ObjectProvider 延迟注入（推荐，是安全的，不会报错）


* 定义xml

```java
    <bean id="user" class="com.spring.ioc.domain.User">
        <property name="id" value="1"/>
        <property name="name" value="binbinshan"/>
    </bean>

    <bean id="super" class="com.spring.ioc.domain.SuperUser" parent="user" primary="true">
        <property name="address" value="beijing"/>
    </bean>
```

使用ObjectProvider延迟注入User
使用ObjectFactory延迟注入set集合，集合类型为User

```java
public class LazyAnnotationDependencyInjectionDemo {

    // 实时注入
    @Autowired
    @Qualifier("user")
    private User user;

    // 延迟注入
    @Autowired
    private ObjectProvider<User> userObjectProvider;

    @Autowired
    private ObjectFactory<Set<User>> usersObjectFactory;

    public static void main(String[] args) {

        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类） -> Spring Bean
        applicationContext.register(LazyAnnotationDependencyInjectionDemo.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);

        String xmlResourcePath = "classpath:/META-INF/dependency-lookup-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);

        // 启动 Spring 应用上下文
        applicationContext.refresh();

        // 依赖查找 QualifierAnnotationDependencyInjectionDemo Bean
        LazyAnnotationDependencyInjectionDemo demo = applicationContext.getBean(LazyAnnotationDependencyInjectionDemo.class);

        System.out.println("demo.user = " + demo.user);
        System.out.println("demo.userObjectProvider = " + demo.userObjectProvider.getObject());
        System.out.println("demo.usersObjectFactory = " + demo.usersObjectFactory.getObject());

        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

}
```

输出：

```java
demo.user = User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
demo.userObjectProvider = SuperUser{address='beijing'} User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
demo.usersObjectFactory = [User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, SuperUser{address='beijing'} User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}]

```

user 使用实时加载，并且 @Qualifier("user")  加载指定id的bean，所以输出的是user。

demo.userObjectProvider 因为类型是User，但是在context上下文中有两个user类型，但是superUser指定了Primary，所以输出的是superUser。

demo.usersObjectFactory 指定了集合类型，所以会加载所有的user类型，也就是user 和 superUser。





------------因下面三个章节篇幅过多，单独进行分析--------------
## @Autowired 注入原理
todo 
## Java通用注解注入原理
todo
## 自定义依赖注入注解
todo




 
 
 
 
