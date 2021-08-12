# Spring Bean 生命周期(一)

- [Spring Bean 生命周期(一)](#spring-bean-生命周期一)
  - [Bean生命周期-总览](#bean生命周期-总览)
  - [Spring Bean 元信息配置阶段](#spring-bean-元信息配置阶段)
    - [面向资源中的xml配置](#面向资源中的xml配置)
    - [面向资源中的properties配置](#面向资源中的properties配置)
    - [面向注解](#面向注解)
    - [面向API](#面向api)
  - [Spring Bean 元信息解析阶段](#spring-bean-元信息解析阶段)
    - [面向资源的BeanDefinition解析](#面向资源的beandefinition解析)
    - [面向注解的BeanDefinition解析](#面向注解的beandefinition解析)
  - [Spring Bean 注册阶段](#spring-bean-注册阶段)
  - [Spring BeanDefinition 合并阶段](#spring-beandefinition-合并阶段)
  - [Spring Bean Class 加载阶段](#spring-bean-class-加载阶段)
  - [参考](#参考)

## Bean生命周期-总览
Spring Bean的生命周期这里一共细分为 18 个阶段，从 Bean 的配置阶段到最终的销毁阶段，还特别加入了垃圾回收。

以下图为例，展示了Bean的一个全流程生命周期：

![Spring bean生命周期全流程图](media/16286845390039/16287474683179.jpg)


因本章内容量过大，因此分为三节来进行讨论：

* 第一节：主要是讨论 Spring Bean 的元信息配置，解析，注册，合并，类加载 5 个阶段，其中后 4 个阶段主要是 Spring 容器内部操作。

* 第二节：主要是讨论 Spring 如何将 Class 进行实例化，以及实例化之后的属性赋值阶段，每个阶段均有对应的接口回调方法进行演示。

* 第三节：主要讨论初始化、销毁以及垃圾回收等 3 个阶段，以及每个阶段对应的接口回调方法。


## Spring Bean 元信息配置阶段

该阶段主要作用是进行 BeanDefinition 配置，而 BeanDefinition 的配置主要有三种形式：
* 面向资源 ：xml配置、properties配置
* 面向注解 ：@Configuration、@Component、@Bean
* 面向API ：BeanDefinitionBuilder

### 面向资源中的xml配置

```java
public class BeanDefinitionByResourceXmlDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器,
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 使用 XmlBeanDefinitionReader 加载配置，参数需要BeanDefinitionRegistry，而DefaultListableBeanFactory 实现了 BeanDefinitionRegistry
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/beanDefinition-context.xml";
        int beanDefinitionsCount = xmlBeanDefinitionReader.loadBeanDefinitions(location);
        //输出加载的beanDefinition个数
        System.out.println(beanDefinitionsCount);

        lookupCollectionByType(beanFactory);
    }
    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listBeanFactory.getBeansOfType(User.class);
            for (Map.Entry<String,User> entry: users.entrySet()) {
                System.out.println("查找到的对象---" + entry.getValue());
            }
        }
    }
}
```

输出：
```java
2
查找到的对象---User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
查找到的对象---SuperUser{address='beijing'} User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```

### 面向资源中的properties配置

```java
public class BeanDefinitionByResourcePropertiesDemo {

    public static void main(String[] args) {
        //创建 BeanFactory 容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //使用 PropertiesBeanDefinitionReader 读取 BeanDefinition
        PropertiesBeanDefinitionReader propertiesBeanDefinitionReader = new PropertiesBeanDefinitionReader(beanFactory);
        //指定EncodedResource ，否则可能出现乱码
        Resource resource = new ClassPathResource("META-INF/default.properties");
        EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
        //Load bean definitions
        propertiesBeanDefinitionReader.loadBeanDefinitions(encodedResource);

        lookupCollectionByType(beanFactory);
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listBeanFactory.getBeansOfType(User.class);
            System.out.println("查找到的所有集合对象---" + users);
        }
    }
}
```

输出：
```java
查找到的所有集合对象---{user=User{id=10, name=''binbinshan'', localResource=null, city=BEIJING, birthCity=null, lifeCity=null, workCity=null}}
```

### 面向注解

```java
//通过 @Import 来进行导入
@Import(BeanDefinitionByAnnotationDemo.Config.class)
public class BeanDefinitionByAnnotationDemo {


    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanDefinitionByAnnotationDemo.class);
        applicationContext.refresh();
        lookupCollectionByType(applicationContext);
        applicationContext.close();
    }


    @Component //定义当前类作为 Spring Bean（组件）
    public static class Config{
        @Bean
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("binbinshan");
            return user;
        }
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listBeanFactory = (ListableBeanFactory) beanFactory;
            System.out.println("所有标注@Component的bean---" + Arrays.toString(listBeanFactory.getBeanNamesForAnnotation(Component.class)));
            System.out.println("所有标注@Bean的bean---" + Arrays.toString(listBeanFactory.getBeanNamesForAnnotation(Bean.class)));
            System.out.println("所有标注@Import的bean---" + Arrays.toString(listBeanFactory.getBeanNamesForAnnotation(Import.class)));
        }
    }
}
```

输出：
```java
所有标注@Component的bean---[com.spring.ioc.beandefiniton.BeanDefinitionByAnnotationDemo$Config]
所有标注@Bean的bean---[user]
所有标注@Import的bean---[beanDefinitionByAnnotationDemo]
```

### 面向API
通过BeanDefinitionBuilder构建BeanDefinion。

使用BeanDefinitionBuilder，通过命名Bean和非命名Bean的方式注册BeanDefinition。

```java
public class BeanDefinitionByApiDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //通过非命名注册Bean
        registerUserBeanDefinition(beanFactory);
        //通过指定名称注册Bean
        registerUserBeanDefinition(beanFactory,"generic-beanName-user");

        Map<String, User> users = beanFactory.getBeansOfType(User.class);
        users.entrySet().stream().forEach(System.out::println);
    }

    public static void registerUserBeanDefinition(BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder
                .addPropertyValue("id", 1L)
                .addPropertyValue("name", "binbinshan");

        if (StringUtils.hasText(beanName)) {
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            //非命名方式
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), registry);
        }
    }
    
    public static void registerUserBeanDefinition(BeanDefinitionRegistry registry) {
        registerUserBeanDefinition(registry, null);
    }
}
```

输出：
```java
com.spring.ioc.domain.User#0=User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
generic-beanName-user=User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```

--------------------------

## Spring Bean 元信息解析阶段

该阶段主要作用是对元信息进行解析，而解析阶段有两种形式：

* 面向资源的BeanDefinition解析 ：BeanDefinitionReader 、Xml解析器-BeanDefintionParser

* 面向注解的BeanDefinition解析 ：AnnotatedBeanDefinitonReader


### 面向资源的BeanDefinition解析
这种形式的解析，就是 Spring Bean 元信息配置阶段 中的 xml 和 properties 文件

### 面向注解的BeanDefinition解析
这里演示的是 Spring 使用 AnnotatedBeanDefinitionReader 如何注册为 BeanDefintion 。

```java
public class AnnotatedBeanDefinitionParserDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
        int beanDefinitionCountBefore = beanFactory.getBeanDefinitionCount();
        //注册当前类（非 @Component class）
        reader.register(AnnotatedBeanDefinitionParserDemo.class);
        int beanDefinitionCountAfter = beanFactory.getBeanDefinitionCount();
        int beanDefinitionCount = beanDefinitionCountAfter - beanDefinitionCountBefore;
        System.out.println("Bean 定义注册的数量："+beanDefinitionCount);
        //Bean 名称生成来自于 BeanNameGenerator,注册实现 AnnotatedBeanNameGenerator
        AnnotatedBeanDefinitionParserDemo demo = beanFactory.getBean("annotatedBeanDefinitionParserDemo",
                AnnotatedBeanDefinitionParserDemo.class);

        System.out.println(demo);
    }
}
```

输出：
```java
Bean 定义注册的数量：1
com.spring.ioc.beandefiniton.AnnotatedBeanDefinitionParserDemo@2ff4f00f
```

## Spring Bean 注册阶段

BeanDefinition 是通过 BeanDefinitionRegistry 接口中的 registerBeanDefinition 方法进行注册的。而 registerBeanDefinition方法的唯一实现是在DefaultListableBeanFactory#registerBeanDefinition中。

在DefaultListableBeanFactory中有几个关键的代码：
```
beanDefinitionMap ：数据结构为 ConcurrentHashMap，没有顺序。
beanDefinitionNames ：数据结构为 ArrayList，存放 beanName 保证注册的顺序
this.beanDefinitionMap.put(beanName, beanDefinition); //将 beanDefinition 存入 beanDefinitionMap
removeManualSingletonName，注册的单例对象（并非 Bean Scope）和注册 BeanDefinition 是一个互斥的操作，只能存在一个
```


源码分析：
```java
@Override
public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
		throws BeanDefinitionStoreException {
    ...
    // beanDefinition 效验
	if (beanDefinition instanceof AbstractBeanDefinition) {
		try {
			((AbstractBeanDefinition) beanDefinition).validate();
    ...
    //是否已经存在 BeanDefinition
	BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
	if (existingDefinition != null) {
        //是否允许重复定义 默认是 true 
		if (!isAllowBeanDefinitionOverriding()) {
			throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
		}
     ...
        // 将 beanDefinition 存入 beanDefinitionMap 中
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}
	else {
        //如果 Bean 已经开始创建
		if (hasBeanCreationStarted()) {
			// Cannot modify startup-time collection elements anymore (for stable iteration)
			synchronized (this.beanDefinitionMap) {//加锁保证操作的安全性
                // 将 beanDefinition 存入 beanDefinitionMap 中
				this.beanDefinitionMap.put(beanName, beanDefinition);
        ...
			}
		}
		else {//正常创建
			// Still in startup registration phase
            // 将 beanDefinition 存入 beanDefinitionMap 中
			this.beanDefinitionMap.put(beanName, beanDefinition);
            //主要是为了注册的顺序
			this.beanDefinitionNames.add(beanName);
            //删除掉注册的单例对象，互斥操作
			removeManualSingletonName(beanName);
		}
   ...
}
```


## Spring BeanDefinition 合并阶段

现在两个Bean，superUser Bean 继承 user bean，当superUser 进行BeanDefinition加载时，就会涉及到合并BeanDefinition。

那么下面通过源码的方式分析下Merged的过程：

合并入口，这是一个接口：
```java
//通过 beanName 返回一个被合并的 BeanDefinition，合并 child bean definition 和它的 parent.
org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
```

getMergedBeanDefinition的实现只有一个，就是org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(java.lang.String)。那来分析这个方法：

```java
	@Override
	public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
		String beanName = transformedBeanName(name);
		//这里的意思是指：
		//containsBeanDefinition(beanName) ： 当前beanFactory是否包含beanName
		//getParentBeanFactory() instanceof ConfigurableBeanFactory) ：类型是否是ConfigurableBeanFactory
		//如果符合条件也就是 包含 并且 类型一致，那么就从当前BeanFactory查找，走这段getMergedLocalBeanDefinition(beanName)
		//如果不符合条件，进行入判断内部(这里是取反)，递归的去父BeanFactory查，如果找不到就会抛出NoSuchBeanDefinitionException
		if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
			return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
		}
		return getMergedLocalBeanDefinition(beanName);
	}
```

我们继续往下，在当前beanFactory中找到了对应的beanName,那么getMergedLocalBeanDefinition方法做了什么呢？
```java
	protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
	   //在 mergedBeanDefinitions 这个集合中查找，这个集合是合并之后的 BeanDefinition 存放的集合，集合元素类型为 RootBeanDefinition
	   //mergedBeanDefinitions 表示的只是当前 BeanFactory 中的 BeanDefinition，如果有多层 BeanFactory ，每个 BeanFactory 都会有这个 mergedBeanDefinitions 的缓存。
		RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
		if (mbd != null && !mbd.stale) {
			return mbd;
		}
		return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
	}
```

当然了，第一次进来肯定是在mergedBeanDefinitions中找不到的，所以会走getMergedBeanDefinition方法。最终进入到这里：

```java
protected RootBeanDefinition getMergedBeanDefinition(
		String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
		throws BeanDefinitionStoreException {
  //加锁的原因：操作既有 get 也有 put，需要加锁保证安全性，而且这个方法可能在很多地方调用
	synchronized (this.mergedBeanDefinitions) {
		RootBeanDefinition mbd = null;
		RootBeanDefinition previous = null;
       //containingBd 表示的是嵌套 Bean 的情况.
       //如果为空，表示当前的 BeanDefintion 并不是一个嵌套 Bean 而是顶层 Bean
		if (containingBd == null) {
		  //我们在进来这个方法之前已经从mergedBeanDefinitions获取了一次，
		  //这里在获取的一次的原因是，防止别的线程在当前线程进入该方法之前又加载了BeanDefinition
			mbd = this.mergedBeanDefinitions.get(beanName);
		}

		if (mbd == null || mbd.stale) {
			previous = mbd;
			if (bd.getParentName() == null) {
				// Use copy of given root bean definition.
				if (bd instanceof RootBeanDefinition) {
				   // 如果是 RootBeanDefinition 直接返回,像user就是根对象
					mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
				}
				else {
				   //如果不是，就构建一个 RootBeanDefinition
					mbd = new RootBeanDefinition(bd);
				}
			}
			else {
			   //而示例中 SuperUser则是继承自user,它不是跟节点，属于这一种情况
				BeanDefinition pbd;
				try {
				    // 获取 parent 属性中 BeanName
					String parentBeanName = transformedBeanName(bd.getParentName());
					// 如果 beanName和parentBeanName不相同
					if (!beanName.equals(parentBeanName)) {
					   // 获取 parent 的合并之后的 BeanDefintion，因为 parent 有可能也是一个被合并的 BeanDefintion
						pbd = getMergedBeanDefinition(parentBeanName);
					}
					else {
					   // 如果相同的话，去 parent BeanFactory 中做层次性的查找
						BeanFactory parent = getParentBeanFactory();
						if (parent instanceof ConfigurableBeanFactory) {
							pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
						}
					}
				}
				......
				// Deep copy with overridden values.
				mbd = new RootBeanDefinition(pbd);
				//这个方法就是进行合并操作
				mbd.overrideFrom(bd);
			}

			...
			// 将合并之后的 mbd 存放到 mergedBeanDefinitions 集合中
			if (containingBd == null && isCacheBeanMetadata()) {
				this.mergedBeanDefinitions.put(beanName, mbd);
			}
		}
		...
		return mbd;
	}
}
```

总结一下：
1. user 和 superUser 都会在合并阶段从 GenericBeanDefinition 变为 RootBeanDefintion。
2. superUser 中的属性会覆盖 user的属性，当然不会改变user本身的属性，而是指覆盖superUser继承过来的属性。


## Spring Bean Class 加载阶段
在有了RootBeanDefintion之后，我们那Bean是怎么加载的呢？

在doGetBean中有createBean
```java
	protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
			@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {
			 ...
			 //前后代码都省略了
			 if (mbd.isSingleton()) {
					sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args);
						}catch (BeansException ex) {
							destroySingleton(beanName);
							throw ex;
						}
					});
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}
			 ...
			}
```


那进入createBean方法看一下：
```java
@Override
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
			throws BeanCreationException {

		...
		// 返回user的Class，之前是字符串类型，处理完之后返回 Class 类型
	   Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
		...
	}
```

继续进入resolveBeanClass：
```java

protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, String beanName, final Class<?>... typesToMatch)
			throws CannotLoadBeanClassException {
        ...
          //判断当前RootBeanDefinition是否已经有class，有就直接返回
			if (mbd.hasBeanClass()) {
				return mbd.getBeanClass();
			}
			//这里是java 安全控制，一般默认没有
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>) () ->
					doResolveBeanClass(mbd, typesToMatch), getAccessControlContext());
			}
			else {
			  //然后执行这里，进入真正获取的地方。
				return doResolveBeanClass(mbd, typesToMatch);
			}
		...
	}
```

doResolveBeanClass中最终会调用resolveBeanClass，而resolveBeanClass就是加载的核心
```java
	@Nullable
	public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) {
			return null;
		}
		//通过 forName 加载这个 Bean 的 Class，得到这个 resolvedClass 的 Class 并返回。
		//而 ClassUtils.forName最终也是通过Class.forName()进行类加载的 因为forName时会触发类加载。
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}
```

所以在 Spring BeanDefinition 变成 Class 的过程其实还是通过传统 Java 的 ClassLoader 来进行加载的。




## 参考
参考：https://blog.csdn.net/xiewenfeng520/article/details/105878401
