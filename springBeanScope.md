# Spring Bean 作用域

- [Spring Bean 作用域](#spring-bean-作用域)
  - [Spring的作用域](#spring的作用域)
  - [Singleton作用域](#singleton作用域)
  - [prototype作用域](#prototype作用域)
    - [依赖查找](#依赖查找)
    - [依赖注入](#依赖注入)
    - [bean的初始化和销毁](#bean的初始化和销毁)
    - [prototype bean 增加销毁生命周期](#prototype-bean-增加销毁生命周期)


在Spring中只需要关注和掌握 singleton 和 prototype，对应设计模式里面的单例模式和原型模式。

request、session、application 主要用于页面渲染，比如 JSP、Velocity、Freemaker 等模板引擎，在现在的 web 开发中已经慢慢的转向前后端分离，模板引擎技术已经慢慢的边缘化了。


## Spring的作用域

|来源|说明|
| --- | --- |
|singleton|默认 Spring Bean 作用域，一个 Beanfactory 有且仅有一个实例|
|prototype|原型作用域，每次依赖查找和依赖注入生成新 Bean 对象|
|request|将 Spring Bean 存储在 ServletRequest 上下文中|
|session|将 Spring Bean 存储在 HttpSession 中|
|application|将 Spring Bean 存储在 ServletContext 中|

## Singleton作用域

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16286431728018.jpg)

1. 从上面的图可以看到，三个 bean 配置都不同，但是 ref="accountDao"，都指向同一个 bean，每次进行属性的注入，都是同一个共享实例。

2. 从设计模式的角度，单例模式不论是“懒汉式”还是“饿汉式”，其实最主要的作用是保证对象是唯一的。

3. 例如在 JVM 的层面来说，我们常用静态变量做单例，每个类对应一个 ClassLoader，ClassLoader 在 load Class 的时候，会加载这个类的静态信息，在同一个 ClassLoader 中，单例对象是唯一的。

4. 而在Spring中，一个 Bean 对象对应一个应用上下文，或者说 Spring IoC 的 BeanFactory 。通常一个 Bean 默认作用域就是 singleton，不需要配置，换言之，在同一个 BeanFactory 中，Bean 对象只有一个。

需要特别注意：Bean 的作用域，并不是指的所有的 Bean，而是指的我们 BeanDefinition。

在BeanDefinition 源码中有两个方法：
```java
boolean isSingleton();
boolean isPrototype();
```

通过这个两个方法可以判断一个 BeanDefinition 是否是单例或者原型，但是并没有 request、session、application 相关的方法，侧面说明这三种不需要过多关注。

singleton 和 prototype 不能简单的说是互斥的关系，因为从接口的角度看，两个方法可以同时存在。


## prototype作用域

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16286459402313.jpg)

每次进行属性注入都会产生一个新的实例，以依赖查找和依赖注入两个示例说明：


### 依赖查找

创建两个Bean，一个singletonUser，一个prototypeUser，然后进行依赖查找。

```java
public class DependencyLookupBeanScopeDemo {


    @Bean
    public static User singletonUser() {
        return createUser(System.nanoTime());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static User prototypeUser() {
        return createUser(System.nanoTime());
    }

    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DependencyLookupBeanScopeDemo.class);
        applicationContext.refresh();

        scopeBeanByLookup(applicationContext);

        applicationContext.close();
    }

    private static void scopeBeanByLookup(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 3; i++) {
            User singletonUser = applicationContext.getBean("singletonUser", User.class);
            System.out.println("singletonUser===" + singletonUser);

            User prototypeUser = applicationContext.getBean("prototypeUser", User.class);
            System.out.println("prototypeUser===" + prototypeUser);
        }
    }
}
```

输出：

```java
singletonUser===User{id=19248869803081, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser===User{id=19248889997115, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser===User{id=19248869803081, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser===User{id=19248890730946, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser===User{id=19248869803081, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser===User{id=19248891128744, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```
可以看出来，单例对象获取三次，每次的对象都是同一个，而多例对象获取三次，每次都是不同的对象。


### 依赖注入

通过@Qualifier限定指定注入user对象，和使用集合注入user对象

```java
public class DependencyInjectionBeanScopeDemo {


    @Bean
    public static User singletonUser() {
        return createUser(System.nanoTime());
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static User prototypeUser() {
        return createUser(System.nanoTime());
    }
    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Autowired
    @Qualifier("singletonUser")
    private User singletonUser1;

    @Autowired
    @Qualifier("singletonUser")
    private User singletonUser2;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser1;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser2;

    @Autowired
    private Map<String, User> users;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DependencyInjectionBeanScopeDemo.class);
        applicationContext.refresh();
        scopeBeanByInjection(applicationContext);
        applicationContext.close();
    }

    private static void scopeBeanByInjection(AnnotationConfigApplicationContext applicationContext) {
        DependencyInjectionBeanScopeDemo demo = applicationContext.getBean(DependencyInjectionBeanScopeDemo.class);
        System.out.println(demo.singletonUser1);
        System.out.println(demo.singletonUser2);
        System.out.println(demo.prototypeUser1);
        System.out.println(demo.prototypeUser2);

        demo.users.entrySet().stream().forEach(System.out::println);
    }
}
```

输出：
```java
User{id=19408740543937, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
User{id=19408740543937, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
User{id=19408743810605, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
User{id=19408744537265, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser=User{id=19408740543937, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser=User{id=19408748431425, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```

依赖注入时，两个注入的单例对象是一致的，两个注入的多例对象是不一致的；而集合中只有一个单例和一个多例的user对象，这里是比较特别的一点。



从依赖查找、依赖注入、到后面的集合依赖注入，singleton bean 只生成了一个对象，而 prototype 每次都会生成一个新的对象。


### bean的初始化和销毁

结论：Spring 容器没有办法管理 prototype Bean 的完整生命周期，销毁回调方法将不会执行，

我们以一个示例来证明：

使用@PostConstruct定义User对象的初始化方法。
使用@PreDestroy定义User对象的销毁方法。
使用BeanNameAware接口进行回调后，获取beanName;
```java
public class User implements BeanNameAware {

    private String beanName;
    ....
    
    @PostConstruct
    public void init() {
        System.out.println(beanName + "用户对象初始化...");
    }

    @PreDestroy
    public void destroy() {
        System.out.println(beanName + "用户对象销毁...");
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
```

重新执行上面依赖查找的代码，此时控制台输出的结果：
```java
singletonUser用户对象初始化...
singletonUser===User{id=19738669097947, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser用户对象初始化...
prototypeUser===User{id=19738688619418, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser===User{id=19738669097947, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser用户对象初始化...
prototypeUser===User{id=19738689588846, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser===User{id=19738669097947, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
prototypeUser用户对象初始化...
prototypeUser===User{id=19738689966354, name='null', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
singletonUser用户对象销毁...
```

通过输出结果，我们可以看出来：
singletonUser只会初始化一次，最后销毁一次。
prototypeUser则每次依赖查找都会初始化一次，而最后没有销毁方法。


### prototype bean 增加销毁生命周期

1. 第一种可以通过 addBeanPostProcessor 实现，在 bean 初始化之后进行一些销毁的处理（但是不建议这么做）。
2. 第二种可以通过实现 DisposableBean 接口，重写 destroy() 方法实现。


第二种示例：

```java
public class PrototypeBeanDestroyDemo implements DisposableBean {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static User prototypeUser() {
        return createUser(System.nanoTime());
    }

    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser1;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser2;

    @Autowired
    private Map<String, User> users;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(PrototypeBeanDestroyDemo.class);
        applicationContext.refresh();
        applicationContext.close();
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("当前 BeanScopeDemo Bean 正在销毁中...");
        this.prototypeUser1.destroy();
        this.prototypeUser2.destroy();
        // 销毁集合中的 Prototype Bean
        for (Map.Entry<String,User> entry:this.users.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            // 判断 beanDefinition 是否是原型模式，因为 Singleton Bean 会自己进行销毁
            if(beanDefinition.isPrototype()){
                User user = entry.getValue();
                user.destroy();
            }
        }
    }

}
```
输出：

```java
prototypeUser用户对象初始化...
prototypeUser用户对象初始化...
prototypeUser用户对象初始化...
当前 BeanScopeDemo Bean 正在销毁中...
prototypeUser用户对象销毁...
prototypeUser用户对象销毁...
prototypeUser用户对象销毁...
```



