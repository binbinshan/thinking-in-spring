# Spring Bean 生命周期(三)

本节对 Spring Bean 初始化阶段以及后面的销毁阶段进行分析和讨论。Aware 回调源码是在 initializeBean 方法中 （初始化 Bean 阶段），所以也可以当成是 Spring Bean 初始化阶段。

![Spring bean生命周期全流程图](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16287474683179.jpg)

## Spring Bean Aware 接口回调阶段

Spring Aware 接口共以下列表，其中此列表中的顺序也是源码的回调执行顺序：

1. BeanNameAware
2. BeanClassLoaderAware
3. BeanFactoryAware
4. EnvironmentAware
5. EmbeddedValueResovlerAware
6. ResourceLoaderAware
7. ApplicationEventPublisherAware
8. MessageSourceAware
9. ApplicationContextAware


其中前三个Aware回调接口使用beanFactory就可以进行回调，而后6个Aware回调接口则需要ApplicationContext才能进行回调。

### 前三个Aware接口

修改UserHolder，实现接口，定义属性，重写方法进行赋值：

```java
public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware {

    private User user;
    private String description;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private String beanName;
    
    ...
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}

```

使用之前的 BeanInstantiationLifecycleDemo 示例代码，执行结果
```java
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='binbinshan v2', classLoader=sun.misc.Launcher$AppClassLoader@18b4aac2, beanFactory=org.springframework.beans.factory.support.DefaultListableBeanFactory@6df97b55: defining beans [user,superUser,userHolder]; root of factory hierarchy, beanName='userHolder'}、
```

### 源码分析

在进行依赖查找时，会调用doCreateBean方法：
```java
	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
			throws BeanCreationException {
        
        ...
        
        try {
          //bean 属性赋值阶段
			populateBean(beanName, mbd, instanceWrapper);
			//bean 初始化bean阶段
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
		...

```
initializeBean中会调用invokeAwareMethods方法对Aware 进行判断。
```java
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
		...
		
		invokeAwareMethods(beanName, bean);

		...
	}
```

而在invokeAwareMethods方法中，就会按照代码顺序依次加载BeanNameAware、BeanClassLoaderAware、BeanFactoryAware的回调方法进行赋值，也就是我们userHolde中实现的方法。

```java
	private void invokeAwareMethods(final String beanName, final Object bean) {
		if (bean instanceof Aware) {
			if (bean instanceof BeanNameAware) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
			if (bean instanceof BeanClassLoaderAware) {
				ClassLoader bcl = getBeanClassLoader();
				if (bcl != null) {
					((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
				}
			}
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
			}
		}
	}
```

### 后六个Aware接口

继续修改userHolder，实现Environment接口，重写方法：

```java
public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware {

    ...

    private Environment environment;
    
    ...

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
```

这个时候继续使用BeanInstantiationLifecycleDemo进行测试，会发现 EnvironmentAware 没有被回调。
这是因为这后六个 Aware 回调接口是 ApplicationContext 生命周期中的，并不在 BeanFactory 生命周期中，这是 BeanFactory 和 ApplicationContext 的一个具体区别之一。

那么我们使用ApplicationContext来进行测试,对BeanInstantiationLifecycleDemo进行重构：

```java
public class BeanInstantiationLifecycleDemo {

    public static void main(String[] args) {
        executeBeanFactory();
        System.out.println("-------------------------分隔符-----------------------");
        executeApplicationContext();
    }

    private static void executeApplicationContext() {
        String[] locations = {"classpath:/META-INF/dependency-lookup-context.xml"};

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(locations);

        //第一种添加 BeanpostBeanProcess 实现方式：
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        //第二种添加 BeanpostBeanProcess 实现方式：
        //将 <bean class="com.huajie.thinking.in.spring.bean.lifecycle.MyInstantiationAwareBeanPostProcess"/> 配置在 xml 中

        applicationContext.refresh();

        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);

        System.out.println(userHolder);

        applicationContext.close();

    }

    private static void executeBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);

        User user = beanFactory.getBean("user", User.class);
        SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);


        System.out.println(user);
        System.out.println(superUser);
        System.out.println(userHolder);
    }

}

```

执行输出：
```java

Bean 定义的数量: 3
User{id=2, name='after-superUser v2', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='binbinshan v2', classLoader=sun.misc.Launcher$AppClassLoader@18b4aac2, beanFactory=org.springframework.beans.factory.support.DefaultListableBeanFactory@5a61f5df: defining beans [user,superUser,userHolder]; root of factory hierarchy, beanName='userHolder', environment=null}
-------------------------分隔符-----------------------
UserHolder{user=SuperUser{address='SHANGHAI'} User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='binbinshan v2', classLoader=sun.misc.Launcher$AppClassLoader@18b4aac2, beanFactory=org.springframework.beans.factory.support.DefaultListableBeanFactory@5622fdf: defining beans [user,superUser,userHolder]; root of factory hierarchy, beanName='userHolder', environment=StandardEnvironment {activeProfiles=[], defaultProfiles=[default], propertySources=[PropertiesPropertySource {name='systemProperties'}, SystemEnvironmentPropertySource {name='systemEnvironment'}]}}

```

可以看出在ApplicationContext中的userHolder输出了environment回调。


### 源码

在ApplicationContext#refresh中会调用prepareBeanFactory方法：
```java
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        ...
		// Configure the bean factory with context callbacks.
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

}
```

再看下 ApplicationContextAwareProcessor 具体实现,ApplicationContextAwareProcessor#invokeAwareInterfaces: 

```java
	private void invokeAwareInterfaces(Object bean) {
		if (bean instanceof EnvironmentAware) {
			((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
		}
		if (bean instanceof EmbeddedValueResolverAware) {
			((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
		}
		if (bean instanceof ResourceLoaderAware) {
			((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
		}
		if (bean instanceof ApplicationEventPublisherAware) {
			((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
		}
		if (bean instanceof MessageSourceAware) {
			((MessageSourceAware) bean).setMessageSource(this.applicationContext);
		}
		if (bean instanceof ApplicationContextAware) {
			((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
		}
	}

```
这里就会调用相对应的Aware回调。


## Spring Bean 初始化前阶段

初始化前阶段已完成以下三个阶段：

1. Bean 实例化
2. Bean 属性赋值
3. Bean Aware 接口回调

初始化前阶段触发，是通过postProcessBeforeInitialization的回调实现的。

在MyInstantiationAwareBeanPostProcess 新增 postProcessBeforeInitialization 的实现:
```java
public class MyInstantiationAwareBeanPostProcess implements InstantiationAwareBeanPostProcessor {
    ...
    /**
     * 初始化前阶段
     * 这个接口实际上是覆盖的 BeanPostProcessor 中的方法
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals(beanName, "userHolder") && UserHolder.class.equals(bean.getClass())) {
            UserHolder user = UserHolder.class.cast(bean);
            user.setDescription("The user holder v3");
            System.out.println("初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3");
        }
        return bean;
    }

}

```

输出：
```java
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
UserHolder{user=SuperUser{address='SHANGHAI'} User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The user holder v3'}
```

### 源码
AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization

```java
	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
        // 同样也是遍历我们所有的 BeanPostProcessor 
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 执行 postProcessBeforeInitialization 获取返回结果 如果为 null 就返回当前 bean
			Object current = processor.postProcessBeforeInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```



## Spring Bean 初始化阶段

Bean 初始化（Initialization）方式为以下三种：

1. @PostConstruct 标注方法
2. 实现 InitializingBean 接口的 afterPropertiesSet() 方法
3. 自定义初始化方法

这里通过展示这三种方式，再次证明我们讨论过的初始化阶段三种方式的顺序：

在userHolder添加三个方法：

```java
    //使用Java 通用注解
    @PostConstruct
    public void init(){
        this.description = "The userHolder v4";
        System.out.println("初始化阶段 : @PostConstruct -> The user holder v4");
    }

    //通过userHolder实现InitializingBean接口，重写afterPropertiesSet方法。
    @Override
    public void afterPropertiesSet() throws Exception {
        this.description = "The userHolder v5";
        System.out.println("初始化阶段 : afterPropertiesSet() -> The user holder v5");
    }
    //通过自定义初始化方法
    public void customInit() throws Exception {
        this.description = "The userHolder v6";
        System.out.println("初始化阶段 : customInit() -> The user holder v6");
    }
    
```

在userHolder bean定义添加初始化方法引用（init-method）：
```java
    <bean id="userHolder" class="com.spring.ioc.domain.UserHolder"
          autowire="constructor" init-method="customInit">
        <property name="description" value="The user holder" />
    </bean>
```

如果我们这个时候输出，结果会是如下：
```java
Bean 定义的数量: 3
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The userHolder v6'}
```

会发现通过Java注册添加的初始化方法 The user holder v4没有执行，这是因为我们没有添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct。
我们修改下输出代码：
```java
    private static void executeBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);
        
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);

        System.out.println(userHolder);
    }
```

这时输出就有v4了：
```java
Bean 定义的数量: 3
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The userHolder v6'}
```

我们从上面输出可以再一次证明，初始化的三种方式的顺序，先执行 @PostConstruct 形式 ，再执行实现 InitializingBean 接口的实现，最后时自定义初始化方法。后面的操作都会覆盖前面的数据。

### 源码

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16290290600312.jpg)

在invokeInitMethods中会调用 afterPropertiesSet 和 invokeCustomInitMethod（自定义方法）。而@PostContruct则是在初始化前的操作中就已经完成了 postProcessBeforeInitialization，具体可见
InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods 333 行


## Spring Bean 初始化后阶段

初始化后操作是通过BeanPostProcessor#postProcessAfterInitialization实现的。

重写 BeanPostProcessor 中的 postProcessAfterInitialization 。

```java

public class MyInstantiationAwareBeanPostProcess implements InstantiationAwareBeanPostProcessor {
    ....
    /**
     * 初始化后阶段
     * 这个接口实际上是覆盖的 BeanPostProcessor 中的方法
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals(beanName, "userHolder") && UserHolder.class.equals(bean.getClass())) {
            UserHolder user = UserHolder.class.cast(bean);
            user.setDescription("The user holder v7");
            System.out.println("初始化后阶段 : postProcessAfterInitialization() -> The user holder v7");
        }
        return bean;
    }

}

```

执行结果：
```java
Bean 定义的数量: 3
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
初始化后阶段 : postProcessAfterInitialization() -> The user holder v7
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The user holder v7'}

```

### 源码

在上面的截图中，也说明了初始化后的方法也是在initializeBean方法中的applyBeanPostProcessorsAfterInitialization。

```java
@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

遍历所有的 BeanPostProcessor 执行 postProcessAfterInitialization 方法。


在源码层面,初始化 Bean 的方法依次进行了四个操作:

Aware回调 : invokeAwareMethods(beanName, bean); 
初始化前 : applyBeanPostProcessorsBeforeInitialization
初始化 : invokeInitMethods
初始化后 : applyBeanPostProcessorsAfterInitialization

这四个操作的代码都在AbstractAutowireCapableBeanFactory#initializeBean()。


## Spring Bean 初始化完成阶段

初始化完成后的操作是通过 ：
Spring 4.1+: SmartInitializingSingleton#afterSingletonsInstantiated 

UserHolder 实现 SmartInitializingSingleton 接口
```java
public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, InitializingBean
, SmartInitializingSingleton {
    ...

    @Override
    public void afterSingletonsInstantiated() {
        this.description = "The userHolder v8";
        System.out.println("初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8");
    }
}

```

如果此时直接运行，afterSingletonsInstantiated 这个方法是不能被回调，这是因为这个接口回调是通过DefaultListableBeanFactory#preInstantiateSingletons进行调用的。

所以我们要手动调用才可以触发回调，又因为DefaultListableBeanFactory实现beanFactory，所以直接使用beanFactory.preInstantiateSingletons触发。

```java
private static void executeBeanFactory() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
    //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
    beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
    XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
    String location = "classpath:/META-INF/dependency-lookup-context.xml";
    int count = reader.loadBeanDefinitions(location);
    System.out.println("Bean 定义的数量: "+count);
    beanFactory.preInstantiateSingletons();
    UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
    System.out.println(userHolder);
}
```

输出：
```java
user用户对象初始化...
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
初始化后阶段 : postProcessAfterInitialization() -> The user holder v7
初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The userHolder v8'}
```

### 注意事项

SmartInitializingSingleton 通常在 ApplicationContext 场景使用，因为在应用上下文启动过程中，AbstractApplicationContext#refresh 中 finishBeanFactoryInitialization(beanFactory); 会显示的调用这个 preInstantiateSingletons() 方法。

preInstantiateSingletons 在 AbstractApplicationContext 场景非常重要，有两层含义

1. 初始化我们所有的 Spring Bean 
   通过 beanDefinitionNames 来遍历我们所有的 BeanDefintion，逐一进行 getBean(beanName) 操作，通过我们的 BeanDefinition 创建 bean 对象，并缓存到 DefaultSingletonBeanRegistry#singletonObjects 中
   
2. 只有当我们的 Spring Bean 全部初始化完成之后，再进行 afterSingletonsInstantiated() 方法的回调


## Spring Bean 销毁前阶段
销毁前操作是通过：
DestructionAwareBeanPostProcessor#postProcessBeforeDestruction

MyDestructionAwareBeanPostProcessor 实现 DestructionAwareBeanPostProcessor，重写 postProcessBeforeDestruction方法。

```java
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals(beanName, "userHolder") && UserHolder.class.equals(bean.getClass())) {
            UserHolder user = UserHolder.class.cast(bean);
            user.setDescription("The user holder v9");
            System.out.println("销毁前阶段 : postProcessBeforeDestruction() -> The user holder v9");
        }
    }
}
```

将MyDestructionAwareBeanPostProcessor添加BeanPostProcess。
并且调用destroyBean方法进行销毁bean的过程。

```java
    private static void executeBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        beanFactory.addBeanPostProcessor(new MyDestructionAwareBeanPostProcessor());
        //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);
        beanFactory.preInstantiateSingletons();
        //User user = beanFactory.getBean("user", User.class);
        //SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        //System.out.println(user);
        //System.out.println(superUser);

        beanFactory.destroyBean("userHolder",userHolder);
        System.out.println(userHolder);

    }
```

输出：
```java
Bean 定义的数量: 3
user用户对象初始化...
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
初始化后阶段 : postProcessAfterInitialization() -> The user holder v7
初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8
销毁前阶段 : postProcessBeforeDestruction() -> The user holder v9
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The user holder v9'}
```

### 源码
调用了 destroyBean方法后最终会进入DisposableBeanAdapter#destroy方法中，循环遍历 beanPostProcessors 执行 postProcessBeforeDestruction 方法
```java
	@Override
	public void destroy() {
		if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
			for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
				processor.postProcessBeforeDestruction(this.bean, this.beanName);
			}
		}
		...
	}
```

## Spring Bean 销毁阶段

Bean 销毁 和 Bean 初始化 一样都是三种方式：

1. @PreDestroy 标注方法
2. 实现 DisposableBean 接口的 destroy() 方法
3. 自定义销毁方法

在userHolder中添加 三种方式实现：

```java

public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, InitializingBean
, SmartInitializingSingleton , DisposableBean {

    ....

    @PreDestroy
    public void preDestroy(){
        this.description = "The userHolder v10";
        System.out.println("销毁阶段 : @PreDestroy -> The user holder v10");
    }

    @Override
    public void destroy() throws Exception {
        this.description = "The userHolder v11";
        System.out.println("初始化阶段 : DisposableBean#destroy -> The user holder v11");
    }

    public void customDestroy(){
        this.description = "The userHolder v12";
        System.out.println("初始化阶段 : customDestroy -> The user holder v12");
    }

}
```

添加usrHolder bean自定义销毁方法(destroy-method)
```java
<bean id="userHolder" class="com.spring.ioc.domain.UserHolder"
      autowire="constructor" init-method="customInit" destroy-method="customDestroy">
    <property name="description" value="The user holder" />
</bean>
```


添加DestructionAwareBeanPostProcessor，因为 @PostConstruct、@PreDestroy 由 CommonAnnotationBeanPostProcessor 触发，但是 BeanPostProcessor 的添加是 FIFO 的模式，所以 DestructionAwareBeanPostProcessor 必须在之前进行添加。

```java
private static void executeBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        beanFactory.addBeanPostProcessor(new MyDestructionAwareBeanPostProcessor());
        //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);
        beanFactory.preInstantiateSingletons();
        //User user = beanFactory.getBean("user", User.class);
        //SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        //System.out.println(user);
        //System.out.println(superUser);

        beanFactory.destroyBean("userHolder",userHolder);
        System.out.println(userHolder);

    }
```

输出：
```java
Bean 定义的数量: 3
user用户对象初始化...
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
初始化后阶段 : postProcessAfterInitialization() -> The user holder v7
初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8
销毁前阶段 : postProcessBeforeDestruction() -> The user holder v9
销毁阶段 : @PreDestroy -> The user holder v10
销毁阶段 : DisposableBean#destroy -> The user holder v11
销毁阶段 : customDestroy -> The user holder v12
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The userHolder v12'}
```

### 源码

DisposableBeanAdapter#destroy，三种销毁操作都会执行，具体如下：

```java
@Override
	public void destroy() {
		if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
		   //这里需要注意 CommonAnnotationBeanPostProcessor 和 我们自定义的 MyDestructionAwareBeanPostProcessor 都是实现了 DestructionAwareBeanPostProcessor。
		   //所以这两个个beanPostProcessor 都会进行处理
		   //又因为CommonAnnotationBeanPostProcessor 会触发 @PreDestroy 销毁方法。
		   //所以这里先执行 @PreDestroy 
			for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
				processor.postProcessBeforeDestruction(this.bean, this.beanName);
			}
		}
		
		...
		//调用DisposableBean接口的实现，
		((DisposableBean) this.bean).destroy();
		
		...
		//调用自定义销毁方法实现
		invokeCustomDestroyMethod(this.destroyMethod);
	}
```



## Spring Bean 垃圾收集

首先bean 销毁了，不一定会被垃圾回收，而进行垃圾回收有下列几种方式：

1. 关闭 Spring 容器（应用上下文）
2. 执行 GC
3. Spring Bean 覆盖 finalize() 方法被回调

UserHolder 覆盖 Object#finalize 方法，因为这个方法会在对象被 GC 的时候调用。
```java
@Override
    public void finalize() throws Throwable {
        System.out.println(beanName + " 被垃圾回收");
    }
```

修改输出方法：
beanFactory.destroySingletons();//销毁掉所有单例对象，保证没有对象的强引用，导致无法 gc

userHolder=null;//对象置为空

System.gc(); //调用 Full gc

```java
  private static void executeBeanFactory() throws InterruptedException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        beanFactory.addBeanPostProcessor(new MyDestructionAwareBeanPostProcessor());
        //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);
        beanFactory.preInstantiateSingletons();
        //User user = beanFactory.getBean("user", User.class);
        //SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        //System.out.println(user);
        //System.out.println(superUser);

        //beanFactory.destroyBean("userHolder",userHolder);
        beanFactory.destroySingletons();
        System.out.println(userHolder);

        userHolder = null;
        System.gc();
        Thread.sleep(5000);
    }
```

输出结果：

```java
Bean 定义的数量: 3
user用户对象初始化...
初始化前阶段 : postProcessBeforeInitialization() -> The user holder v3
初始化阶段 : @PostConstruct -> The user holder v4
初始化阶段 : afterPropertiesSet() -> The user holder v5
初始化阶段 : customInit() -> The user holder v6
初始化后阶段 : postProcessAfterInitialization() -> The user holder v7
初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8
销毁前阶段 : postProcessBeforeDestruction() -> The user holder v9
销毁阶段 : @PreDestroy -> The user holder v10
销毁阶段 : DisposableBean#destroy -> The user holder v11
销毁阶段 : customDestroy -> The user holder v12
user用户对象销毁...
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='The userHolder v12'}
userHolder 被垃圾回收
```




