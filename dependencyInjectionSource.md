# Spring IoC 依赖来源


- [Spring IoC 依赖来源](#spring-ioc-依赖来源)
  - [依赖查找的来源](#依赖查找的来源)
  - [依赖注入的来源](#依赖注入的来源)
  - [ResolvableDependency](#resolvabledependency)
    - [为什么通过 registerResolvableDependency 方法注册的 无法依赖查找？](#为什么通过-registerresolvabledependency-方法注册的-无法依赖查找)
  - [Spring 容器管理和游离对象](#spring-容器管理和游离对象)
  - [Spring BeanDefinition 作为依赖来源](#spring-beandefinition-作为依赖来源)
  - [內建 Spring BeanDefintion 作为依赖来源](#內建-spring-beandefintion-作为依赖来源)
  - [单例对象 作为依赖来源](#单例对象-作为依赖来源)
  - [内建单例对象 作为依赖来源](#内建单例对象-作为依赖来源)
  - [非 Spring 容器管理对象 作为依赖来源](#非-spring-容器管理对象-作为依赖来源)
    - [registerResolvableDependency 示例](#registerresolvabledependency-示例)
    - [采用BeanFactoryPostProcessor](#采用beanfactorypostprocessor)
  - [外部化配置 作为依赖来源](#外部化配置-作为依赖来源)



前面两个章节讨论了 Spring依赖查找和依赖注入。那么关于这两种方式的依赖从哪里来的，是我们本章讨论的重点。


## 依赖查找的来源

依赖查找能查找到依赖有以下几个来源：

1. 自定义 Spring BeanDefinition

    > 手动去注入一个Bean 
    
    > XML 配置元信息 : \<bean id="user" class="org.geekbang...User"\>
    > Java 注解配置元信息 : @Bean public User user(){...}
    > Java API 配置元信息 : BeanDefinitionRegistry#registerBeanDefinition
    
1. 內建 Spring BeanDefintion
    
    > Spring在创建容器时会注入一些内建的Bean,如：
    
    > ConfigurationClassPostProcessor对象 用于处理 Spring 配置类 
    > AutowiredAnnotationBeanPostProcessor对象 用于处理 @Autowired、@Value
    > EventListenerMethodProcessor对象 用于处理标注 @EventListener 的Spring 事件监听方法 

1. 单例对象
   通过SingletonBeanRegistry#registersiagleton注册单例对象
   
3. 内建单例对象
    
    > Spring在创建容器时会注入一些內建单例对象,如：
    
    > Environment对象 用于外部化配置以及 Profiles。
    > MessageSource对象 用于国际化文案。
    > ApplicationEventMulticaster对象 用于Spring 事件广播器。
    > SystemProperties对象 用于Java 系统属性。
    > SystemEnvironment对象 用于操作系统环境变量。
    > lifecycleProcessor对象 用于Lifecycle Bean 处理器。


## 依赖注入的来源
依赖注入的依赖来源与依赖查找的依赖来源大部分相似，只是依赖注入多了两个来源 非Spring 容器管理对象 和 外部化配置。

1. 自定义 Spring BeanDefinition
2. 內建 Spring BeanDefintion
3. 单例对象
4. 非 Spring 容器管理对象(ResolvableDependency)
    * 对象不存在 spring 容器中（即通过 getBean 的方法无法查找），但是可以依赖注入
5. @Value


## ResolvableDependency

在启动Spring容器时，通过 AbstractApplicationContext#prepareBeanFactory 会注入一些非 Spring 容器管理对象。

```java
beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
beanFactory.registerResolvableDependency(ResourceLoader.class, this);
beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
beanFactory.registerResolvableDependency(ApplicationContext.class, this);
```

通过registerResolvableDependency()，可以将 这个 4 个对象放到 resolvableDependencies 这个 ConcurrentHashMap 中。

这四个对象中 BeanFactory 注册的对象是 beanFactory（通过 getBeanFactory() 方法获取），而其他三个对象注册的是 this（即当前的应用上下文 ApplicationContext），所以这三个对象其实是一致的。

这 4 个对象都是非 Spring 容器管理对象，通过 registerResolvableDependency 方法注册，所以通过 BeanFactory#getBean() 依赖查找无法获取，抛出 NoSuchBeanDefinitionException 异常。

### 为什么通过 registerResolvableDependency 方法注册的 无法依赖查找？
在DefaultListableBeanFactory 中分别用两个对象来进行存储：

1. beanDefinitionMap 用来存储一般注册 BeanDefinition，比如 xml，注解，API
2. resolvableDependencies 用来存储 非 Spring 容器管理对象（或者叫游离对象）

在beanFactory.getBean 会去 beanDefinitionMap 中查找依赖

```java
	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition bd = this.beanDefinitionMap.get(beanName);
		if (bd == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No bean named '" + beanName + "' found in " + this);
			}
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return bd;
	}
```


## Spring 容器管理和游离对象

我们现在已经大概知道了，依赖查找和依赖注入的依赖来源类型，那么按照大类会分成以下三类，通过一系列的指标来帮助理解之间的差异：

|来源|Spring Bean 对象|生命周期管理|配置元信息|使用场景|
|---|---|---|---|---|
|Spring BeanDefinition|是|是|有|依赖查找、依赖注入|
|单体对象|是|否|无|依赖查找、依赖注入|
|ResolvableDependency|否|否|无|依赖注入|


## Spring BeanDefinition 作为依赖来源

要素：

* 元数据：BeanDefinition
* 注册：BeanDefinitionRegistry#registrerBeanDefinition
* 类型：延迟和非延迟
* 顺序：Bean 生命周期顺序按照注册顺序

DefaultListableBeanFactory#registerBeanDefinition 相关源码大致流程：

<div align="center"> <img src="https://github.com/binbinshan/thinking-in-spring/blob/master/images/16285786557137.jpg" width="1200px"> </div><br>

## 內建 Spring BeanDefintion 作为依赖来源

当我们**激活注解驱动**的时候，这些 Bean 就会通过内建的方式放到我们的应用上下文中。

相关源码位置：AnnotationConfigUtils#registerAnnotationConfigProcessors()

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
		BeanDefinitionRegistry registry, @Nullable Object source) {
	...
	Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);

	if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
	}

	if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
	}

	// Check for JSR-250 support, and if present add the CommonAnnotationBeanPostProcessor.
	if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
	}

	// Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
	if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition();
		try {
			def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
					AnnotationConfigUtils.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException(
					"Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
		}
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
	}

	if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
	}

	if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
		RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
		def.setSource(source);
		beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
	}

	return beanDefs;
}

```

## 单例对象 作为依赖来源
BeanDefinition 是 Bean 的定义，或者是实例化和初始化的元信息，单例对象只是其中一种 Bean 对象的存在方式。

* 要素
    * 来源：外包普通 Java 对象（不一定是 POJO）
    * 注册：SingletonBeanRegistry#registerSingleton

* 限制
    * 无生命周期管理
    * 无法实现延迟初始化 Bean


源码位置：DefaultSingletonBeanRegistry#registerSingleton

简单的 put 操作，将对象存放到 singletonObjects 中。

```java
	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");
		//主要是这个方法既调用了 get() 方法，又调用了 add() 方法，为了线程安全加锁
		synchronized (this.singletonObjects) {
			Object oldObject = this.singletonObjects.get(beanName);
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
			addSingleton(beanName, singletonObject);
		}
	}

	protected void addSingleton(String beanName, Object singletonObject) {
	   //方法可能被别的地方调用，而且此方法也是一个多元的操作，为了线程安全
		synchronized (this.singletonObjects) {
		   //加入 singletonObjects 集合
			this.singletonObjects.put(beanName, singletonObject);
			//ObjectFactory 操作的 Bean,用来进行延迟查找，如果这个 Bean 注册成单例对象，和这个是一个互斥的操作，所以需要删除
			this.singletonFactories.remove(beanName);
			//这个早期的 SingletonObject 也是一个互斥的操作，所以需要删除
			this.earlySingletonObjects.remove(beanName);
			//为了保存bean的顺序
			this.registeredSingletons.add(beanName);
		}
	}
```

依赖查找 还是通过 getBean 方法，先查询的是 getSingleton() 


## 内建单例对象 作为依赖来源

相关源码位置：AbstractApplicationContext#prepareBeanFactory()

注册一些内建的 Bean，如果引入更多的模块，比如 AOP、事务，也会有相应通过 registerSingleton 注册 Bean。

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		...
		// Register default environment beans.
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}
```


## 非 Spring 容器管理对象 作为依赖来源
* 要素
    * 注册：ConfigurableListableBeanFactory#registerResolvableDependency
        * 只有类型注入一种
        * 只能实现依赖注入
* 限制
    * 无生命周期管理
    * 无法实现延迟初始化 Bean
    * 无法通过依赖查找

### registerResolvableDependency 示例

这里举例说明，非Spring容器管理对象作为依赖来源

```java
public class ResolvableDependencySourceDemo {
    @Autowired
    private String name;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo.class);
        //把当前类 bean 加入到容器中
        applicationContext.refresh();

        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
            configurableListableBeanFactory.registerResolvableDependency(String.class, "binbinshan");
        }
        ResolvableDependencySourceDemo bean = applicationContext.getBean(ResolvableDependencySourceDemo.class);
        System.out.println(bean.name);
        applicationContext.getBean(String.class);
        applicationContext.close();
    }
}
```

这段代码执行会报NoSuchBeanDefinitionException错，这是因为这段代码：
```java
applicationContext.register(ResolvableDependencySourceDemo.class);
//把当前类 bean 加入到容器中
applicationContext.refresh();
```
因为 applicationContext.refresh(); 执行过程中会去触发依赖注入过程，而 @Autowired 标注的属性 name 还没有对象可以注入。所以会报错。可以将这两段代码交换顺序。


重新执行代码后，可以顺利打印name的值为binbinshan, 但是applicationContext.getBean(String.class);会报错，这是因为我们采用了registerResolvableDependency的方式注入依赖，只能依赖不能查找，这也印证了前面的讨论。


### 采用BeanFactoryPostProcessor

通过添加BeanFactoryPostProcessor，在 AbstractApplicationContext#refresh 的源码中有相关的容器生命周期设计；我们添加的BeanFactory 的后置处理，会在invokeBeanFactoryPostProcessors()这个里面进行处理，而依赖注入过程在这之后的 finishBeanFactoryInitialization()。

代码如下：

```java
public class ResolvableDependencySourceDemo2 {
    @Autowired
    private String name;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo2.class);
        //第二种方式
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerResolvableDependency(String.class,"binbinshan");
        });
        applicationContext.refresh();
        ResolvableDependencySourceDemo2 bean = applicationContext.getBean(ResolvableDependencySourceDemo2.class);
        System.out.println(bean.name);
        applicationContext.close();
    }
}
```


## 外部化配置 作为依赖来源

* 要素
    * 类型：非常规 Spring 对象依赖来源
* 限制
    * 无生命周期管理
    * 无法实现延迟初始化 Bean
    * 无法通过依赖查找

在 resources/META-INF 目录下面新建一个 default.properties 的配置文件


```java
@Configuration
@PropertySource(value = "META-INF/default.properties", encoding = "gbk")
public class ExternalConfigurationDependencyDemo {

    @Value("${bin.id:-1}")
    private Long id;

    @Value("${bin.name}")
    private String name;

    @Value("${bin.resource:classpath://default.properties}")
    private Resource resource;

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct-> id: " + id);
        System.out.println("PostConstruct-> resource: " + resource);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ExternalConfigurationDependencyDemo.class);
        applicationContext.refresh();
        ExternalConfigurationDependencyDemo demo = applicationContext.getBean(ExternalConfigurationDependencyDemo.class);

        System.out.println(demo.id);
        System.out.println(demo.name);
        System.out.println(demo.resource);

        applicationContext.close();
    }
}

```

发现可以通过外挂文件，注入对应的配置。