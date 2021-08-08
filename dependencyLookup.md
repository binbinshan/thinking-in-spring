# Spring 依赖查找

- [Spring 依赖查找](#spring-依赖查找)
  - [1. 依赖查找的前世今生](#1-依赖查找的前世今生)
  - [2. 单一类型依赖查找](#2-单一类型依赖查找)
    - [实时依赖查找](#实时依赖查找)
    - [延迟依赖查找](#延迟依赖查找)
  - [3. 集合类型依赖查找](#3-集合类型依赖查找)
  - [4. 层次性依赖查找](#4-层次性依赖查找)
  - [5. 延迟依赖查找](#5-延迟依赖查找)
  - [6. 安全依赖查找](#6-安全依赖查找)
  - [7. 内建可查找的依赖](#7-内建可查找的依赖)
  - [8. 依赖查找中的经典异常](#8-依赖查找中的经典异常)



## 1. 依赖查找的前世今生
在JavaEE中也是存在依赖查找的，包括JNDI、JavaBEans，Spring中的BeanFactory很多也是参考BeanContext 实现的。

* 单一类型依赖查找
    * JNDI - javax.naming.Context#lookup(javax.naming.Name) 
    * JavaBeans - java.beans.beancontext.BeanContext 
* 集合类型依赖查找
    * java.beans.beancontext.BeanContext 
* 层次性依赖查找
    * java.beans.beancontext.BeanContext

    
## 2. 单一类型依赖查找

单一类型依赖查找要使用接口 BeanFactory。

三种依赖查找的方式：

* 根据 Bean 名称查找
    * getBean(String)

* 根据 Bean 类型查找
    * Bean 实时查找
        * getBean(Class)
    * Spring 5.1 Bean 延迟查找
        * getBeanProvider(Class)
        * getBeanProvider(ResolvableType) 
    
* 根据 Bean 名称 + 类型查找
    * getBean(String,Class)


### 实时依赖查找
以代码为例展示三种实时依赖查找：

定义Person，通过 @Component 方式,定义当前类作为 Spring Bean（组件），并且使用@PostConstruct构造初始化；
```java
@Component
@Getter
@Setter
public class Person {
    private Long id;
    private String name;

    @PostConstruct
    private void init(){
        this.id = 100L;
        this.name = "人";
    }
}
```

构建容器，启动后通过getBean获取对象
```java
public static void main(String[] args) {
    //通过注解上下文构建IOC容器
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    //注册 Person bean
    applicationContext.register(Person.class);
    //调用register() 需要手动refresh()
    applicationContext.refresh();

    //1. 根据 Bean 名称查找
    Person lookupPersonByName = (Person) applicationContext.getBean("person");
    System.out.println(lookupPersonByName);
    //2. 根据 Bean 类型查找
    Person lookupPersonByType = applicationContext.getBean(Person.class);
    System.out.println(lookupPersonByType);
    //3. 根据 Bean 名称 + 类型查找
    Person lookupPersonByNameAndType = applicationContext.getBean("person",Person.class);
    System.out.println(lookupPersonByNameAndType);
}
```

### 延迟依赖查找

下列代码是以延迟依赖查找：

```java
public class SingleBeanProviderDemo {
    public static void main(String[] args) {
        //通过注解上下文构建IOC容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        //注册 SingleBeanProviderDemo
        applicationContext.register(SingleBeanProviderDemo.class);
        //调用register() 需要手动refresh()
        applicationContext.refresh();

        lookupByObjectProvider(applicationContext);
    }

    @Bean
    public String helloWorld() { // 方法名就是 Bean 名称 = "helloWorld"
        return "Hello,World";
    }
   
   //getBeanProvider获取Bean
    private static void lookupByObjectProvider(AnnotationConfigApplicationContext applicationContext) {
        //查询String类型的Bean
        ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
        System.out.println(objectProvider.getObject());
    }
}

```


## 3. 集合类型依赖查找

集合类型依赖查找接口 - ListableBeanFactory

* 根据 Bean 类型查找
    * 获取同类型 Bean 名称列表
        * getBeanNamesForType(Class)
        * Spring 4.2 getBeanNamesForType(ResolvableType)

    * 获取同类型 Bean 实例列表
        * getBeansOfType(Class) 以及重载方法 

* 通过注解类型查找
    * Spring 3.0 获取标注类型 Bean 名称列表
    * getBeanNamesForAnnotation(Class<? extends Annotation>)
    
    * Spring 3.0 获取标注类型 Bean 实例列表
    * getBeansWithAnnotation(Class<? extends Annotation>)
    
    * Spring 3.0 获取指定名称 + 标注类型 Bean 实例
    * findAnnotationOnBean(String,Class<? extends Annotation>)

这里就不详细展开了，就是常用的方法调用。

## 4. 层次性依赖查找

层次性依赖查找接口 - HierarchicalBeanFactory

* 双亲 BeanFactory：getParentBeanFactory()

* 层次性查找
    * 根据 Bean 名称查找 : 基于 containsLocalBean 方法实现

    * 根据 Bean 类型查找实例列表
        * 单一类型：BeanFactoryUtils#beanOfType
        * 集合类型：BeanFactoryUtils#beansOfTypeIncludingAncestors
    
    * 根据 Java 注解查找名称列表
        * BeanFactoryUtils#beanNamesForTypeIncludingAncestors

## 5. 延迟依赖查找
 
* org.springframework.beans.factory.ObjectFactory

* org.springframework.beans.factory.ObjectProvider

    * Spring 5 对 Java 8 特性扩展

        * 函数式接口
        * getIfAvailable(Supplier)
        * ifAvailable(Consumer)

* Stream 扩展 - stream()


下列代码为ObjectProvider的延迟依赖查找，这里延迟依赖查找不是实时查找，但是Bean是实时加载的。

```java
@Bean
@Primary
public String helloWorld() { // 方法名就是 Bean 名称 = "helloWorld"
    return "Hello,World";
}

@Bean
public String message() {
    return "Message";
}

//输出Hello,World
private static void lookupByObjectProvider(AnnotationConfigApplicationContext applicationContext) {
    ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
    System.out.println(objectProvider.getObject());
}

//输出 Hello,World Message
private static void lookupByStreamOps(AnnotationConfigApplicationContext applicationContext) {
    ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
    objectProvider.stream().forEach(System.out::println);
}
```

然后针对getIfAvailable(Supplier)在进行一个单独说明，该方法的意思是如果没有可用Bean，可以再supplier指定一个兜底的操作，例如：
```java
private static void lookupIfAvailable(AnnotationConfigApplicationContext applicationContext) {
    ObjectProvider<User> userObjectProvider = applicationContext.getBeanProvider(User.class);
    User user = userObjectProvider.getIfAvailable(User::createUser);
    System.out.println("当前 User 对象：" + user);
}
```
userObjectProvider.getIfAvailable(User::createUser);
这段代码指，如果找不到对应的User类型的Bean，就会调用User的静态方法createUser，创建一个User**对象**(不是Bean)。

## 6. 安全依赖查找
安全的依赖查找是指，当进行依赖查找时，如果没有找到Bean，是否会报错，安全的依赖查找不会报错，不安全的依赖查找会报错。


* 单一类型查找：

    BeanFactory#getBean -> 实时查找 -> 不安全
    
    ObjectFactory#getObject -> 延迟查找 -> 不安全
    
    ObjectProvider#getIfAvailable -> 延迟查找 -> 安全


* 集合类型查找
    ListableBeanFactory#getBeansOfType -> 安全

    ObjectProvider#stream  -> 延迟查找 -> 安全

* 层次性依赖查找的安全性取决于其扩展的单一或集合类型的 BeanFactory 接口

## 7. 内建可查找的依赖
在Spring中容器会帮我们内建一些可查找的依赖。我们可以使用这些依赖。

AbstractApplicationContext 内建可查找的依赖 ：

|Bean 名称|Bean 实例|使用场景|
|---|---|---|
|environment|Environment 对象|外部化配置以及 Profiles|
|systemProperties|java.util.Properties 对象|Java 系统属性|
|systemEnvironment|java.util.Map 对象|操作系统环境变量|
|messageSource|MessageSource 对象|国际化文案|
|lifecycleProcessor|LifecycleProcessor 对象|Lifecycle Bean 处理器|
|applicationEventMulticaster|ApplicationEventMulticaster 对象|Spring 事件广播器|

注解驱动 Spring 应用上下文内建可查找的依赖，可以参考org.springframework.context.annotation.AnnotationConfigUtils中，会帮我们注入这些依赖：

|Bean 名称|Bean 实例|使用场景|
|---|---|---|
|org.springframework.context.annotation.internalConfigurationAnnotationProcessor|ConfigurationClassPostProcessor 对象|处理 Spring 配置类|
|org.springframework.context.annotation.internalAutowir edAnnotationProcessor|AutowiredAnnotationBeanPostProcessor 对象|处理 @Autowired 以及 @Value 注解|
|org.springframework.context.annotation.internalCommo nAnnotationProcessor|CommonAnnotationBeanPostProcessor对象|（条件激活）处理 JSR-250 注解， 如 @PostConstruct等|
|org.springframework.context.event.internalEventListenerProcessor|EventListenerMethodProcessor 对象|处理标注 @EventListener 的 Spring 事件监听方法|
|org.springframework.context.event.internalEventListenerFactory|DefaultEventListenerFactory 对象|@EventListener 事件监听方法适配为 ApplicationListener|
|org.springframework.context.annotation.internalPersistenceAnnotationProcessor|PersistenceAnnotationBeanPostProcessor 对象|（条件激活）处理 JPA 注解场景|


## 8. 依赖查找中的经典异常

这些异常都是继承 java.lang.RuntimeException 实现的。

|异常类型|触发条件（举例）|场景举例|
|---|---|---|
|NoSuchBeanDefinitionException|当查找 Bean 不存在于 IoC 容器时|BeanFactory#getBean ObjectFactory#getObject|
|NoUniqueBeanDefinitionException|类型依赖查找时，IoC 容器存在多个 Bean 实例|   BeanFactory#getBean(Class)|
|BeanInstantiationException|当 Bean 所对应的类型非具体类时|BeanFactory#getBean|
|BeanCreationException|当 Bean 初始化过程中|Bean 初始化方法执行异常时|
|BeanDefinitionStoreException|当 BeanDefinition 配置元信息非法时|XML 配置资源无法打开时|


