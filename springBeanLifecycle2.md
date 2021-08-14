# Spring Bean 生命周期(二)

Spring Bean的生命周期这里一共细分为 18 个阶段，从 Bean 的配置阶段到最终的销毁阶段，还特别加入了垃圾回收。

主要是讨论 Spring 如何将 Class 进行实例化，以及实例化之后的属性赋值阶段。

![Spring bean生命周期全流程图](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16287474683179.jpg)


## Spring Bean 实例化前阶段
Spring Bean 实例化前的操作是通过org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor 接口中实现 postProcessBeforeInstantiation 来进行的。（这个阶段在实际工作中很少会用到）。

这个 Bean 的前置处理在目标 bean 实例化之前，返回一个 bean 的对象可能是一个代理对象去替换这个目标的 bean。

具体操作如下：
1. 自定义类来实现 InstantiationAwareBeanPostProcessor 接口，重写 postProcessBeforeInstantiation 方法
2. 在这个方法中，我们根据 BeanName 来拦截 superUser，替换成一个新的 SuperUser 对象
3. 通过 beanFactory.addBeanPostProcessor 来增加我们的实例化前的操作

代码如下：
```java
public class BeanInstantiationLifecycleDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);

        User user = beanFactory.getBean("user", User.class);
        SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);

        System.out.println(user);
        System.out.println(superUser);
    }


    static class MyInstantiationAwareBeanPostProcess implements InstantiationAwareBeanPostProcessor {
        /**
         * 实例化前阶段
         */
        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals(beanName, "superUser") && beanClass.equals(SuperUser.class)) {
                SuperUser user = new SuperUser();
                user.setName("binbinshan-b");
                user.setAddress("BEIJING");
                return user;
            }
            return null;
        }
    }
}
```

输出：
```java
Bean 定义的数量: 2
User{id=1, name='binbinshan', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```



### 源码分析

上述代码实例化前操作的代码会进入 AbstractAutowireCapableBeanFactory#createBean() 中

```java

@Override
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
			throws BeanCreationException {
      ...
		try {
			//让BeanPostProcessors有机会返回一个代理而不是目标bean实例。
			//superUser创建走这里，会直接返回一个Bean,后续不再执行
			Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
			if (bean != null) {
				return bean;
			}
		}
		
		...

		try {
		   //正常创建Bean user创建是走这里
			Object beanInstance = doCreateBean(beanName, mbdToUse, args);
			if (logger.isTraceEnabled()) {
				logger.trace("Finished creating instance of bean '" + beanName + "'");
			}
			return beanInstance;
		}
```

那么 实现的接口方法 postProcessBeforeInstantiation 应该是在 resolveBeforeInstantiation() 方法中被执行。

```java
	@Nullable
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
		Object bean = null;
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
				    //会进入这里
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			mbd.beforeInstantiationResolved = (bean != null);
		}
		return bean;
	}
```

而最终调用我们自定义逻辑的代码在AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation()中，源码如下：

```java
	@Nullable
	protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

代码比较简单，遍历我们所有的 BeanPostProcessor，如果 BeanPostProcessor 类型为 InstantiationAwareBeanPostProcessor ，执行并返回执行结果。

```

### 结论
原本我们 Spring 默认实例化的 Bean 会被替换成一个代理对象。

如果这个阶段返回了我们替换之后的对象，后面的 Spring Bean 其他生命周期其他过程都不会执行。

所以这是一个非主流操作，不建议使用。


## Spring Bean 实例化阶段

在上面分析的，如果没有实例化前操作，那么Spring Bean 默认实例化的方法是 doCreateBean()。
而在doCreateBean() 中 是 createBeanInstance() 进行实例化的。
我们就接着 createBeanInstance 进行分析。

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    //将bean类名解析为class引用，并将class保存在bean属性中
	Class<?> beanClass = resolveBeanClass(mbd, beanName);
    ...
    // 通过 java8 中的函数式接口进行创建
	Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
	if (instanceSupplier != null) {
		return obtainFromSupplier(instanceSupplier, beanName);
	}
    // 通过 FactoryMethod 方式进行创建
	if (mbd.getFactoryMethodName() != null) {
		return instantiateUsingFactoryMethod(beanName, mbd, args);
	}
    ...
	// 这里四个条件分别是：
	// 可以提供一些构造器的选择策略 ctors 通常为 null
    // 自动绑定为构造器绑定
    // 或者 beanDefintion 中包含构造器参数
    // 获取 args 不为空
    // 满足其中一个就可以
	Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
	if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
			mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
		return autowireConstructor(beanName, mbd, ctors, args);
	}
   // 是否有默认的偏好构造器
	ctors = mbd.getPreferredConstructors();
	if (ctors != null) {
		return autowireConstructor(beanName, mbd, ctors, null);
	}

	// 非特殊方式：简单实用无参构造器
	return instantiateBean(beanName, mbd);
}
```

上面源码分析中，从源码分析中可以知道有两种实例化的方式。
1. 如果我们通过构造器依赖注入(autowire="constructor")，那么就会进入autowireConstructor 
2. 如果是简单实用无参构造器则会进入instantiateBean()


### instantiateBean

我们先分析instantiateBean

```java
	protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			final BeanFactory parent = this;
			//java 安全相关，一般等于 null
			if (System.getSecurityManager() != null) {
				beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						getInstantiationStrategy().instantiate(mbd, beanName, parent),
						getAccessControlContext());
			}
			else {
			//主要是这里 instantiate
				beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
			}
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
			initBeanWrapper(bw);
			return bw;
		}
		...
	}
```

分析instantiate,这里只列举关键代码

```java
	public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {

    //获取无参构造器
	constructorToUse = clazz.getDeclaredConstructor();
    
    //进行实例化，其实就是 Java 反射中的方法。
    return BeanUtils.instantiateClass(constructorToUse);
}
```

最后获取到实例化之后的 User 对象，被封装成 BeanWrapper 对象。
```java
	protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			final BeanFactory parent = this;
            ...
            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
            // 封装成 BeanWrapper 对象
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            // 初始化 BeanWrapper 对象
			initBeanWrapper(bw);
			return bw;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
		}
	}
```

通过无参的构造器实例化完成，所有的属性都是 null，这就是 Spring Bean 实例化的过程，此时还没有进行属性赋值和初始化相关的过程。

### autowireConstructor

```java
public class UserHolder {

    private User user;

    public UserHolder(User user) {
        this.user = user;
    }
}

---------------------

<bean id="userHolder" class="UserHolder" autowire="constructor"/>
```

当通过构造器依赖注入是，就会走autowireConstructor这段代码。

那么在createBeanInstance()中调用 autowireConstructor() 会进入如下源码,也是只分析关键代码：

```java
	public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
			@Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {

		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);

		Constructor<?> constructorToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;
      
       ...

		if (constructorToUse == null || argsToUse == null) {
			// Take specified constructors, if any.
			Constructor<?>[] candidates = chosenCtors;
			if (candidates == null) {
			   //获取beanDefintion的class，这个 class 就是我们加载阶段从 String 替换成的 Class
				Class<?> beanClass = mbd.getBeanClass();
				try {
				//默认为true 返回所有的构造器（没有限定修饰符）
					candidates = (mbd.isNonPublicAccessAllowed() ?
							beanClass.getDeclaredConstructors() : beanClass.getConstructors());
				}
				...
				//catch
			}
          //只有一个构造函数，并且满足其他条件才会进入。
			if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Constructor<?> uniqueCandidate = candidates[0];
				//构造器如果带参数就不会进入，这里只无参构造
				if (uniqueCandidate.getParameterCount() == 0) {
					synchronized (mbd.constructorArgumentLock) {
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			//确定是构造器注入 autowiring = ture
			boolean autowiring = (chosenCtors != null ||
					mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			ConstructorArgumentValues resolvedValues = null;

			...
			//对构造器进行排序
			AutowireUtils.sortConstructors(candidates);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Constructor<?>> ambiguousConstructors = null;
			LinkedList<UnsatisfiedDependencyException> causes = null;

			for (Constructor<?> candidate : candidates) {
             ...
				ArgumentsHolder argsHolder;
				// 获取参数类型
				Class<?>[] paramTypes = candidate.getParameterTypes();
				//对构造器的参数进行分析
				if (resolvedValues != null) {
					try {
					// 获取 ConstructorProperties注解 配置的参数名称
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
						// 获取参数名称处理器
						if (paramNames == null) {
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								paramNames = pnd.getParameterNames(candidate);
							}
						}
						//触发依赖处理过程,也就是 beanFactory.resolveDependency
						argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
								getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
					}
			...
			
		Assert.state(argsToUse != null, "Unresolved constructor arguments");
		// 设置 BeanWrapper 的 BeanInstance，并返回
		bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
		return bw;
	}

```

结论：构造器注入默认是按照类型注入，底层方法是 BeanFactory#resolveDependecy()


## Spring Bean 实例化后阶段

我们在上面示例的基础上，在 MyInstantiationAwareBeanPostProcess 新增一个 postProcessAfterInstantiation 的重载方法

```java
        /**
         * 实例化后阶段
         */
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals(beanName, "user") && User.class.equals(bean.getClass())) {
                //"user" 对象不允许属性赋值（配置元信息-> 属性值）
                User user = User.class.cast(bean);
                user.setId(2L);
                user.setName("after-superUser v2");
                return false;//返回 false 表示忽略掉配置元信息，比如 <bean ...<property name = id value = 1/>
            }
            return true;
        }
    }
```

输出：
```java
Bean 定义的数量: 2
User{id=2, name='after-superUser v2', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}
```

User 对象的 id 和 name 已经发生了变化。


### 源码分析
实例化后操作是在AbstractAutowireCapableBeanFactory#populateBean 中执行的：

进入到方法中，这里逻辑比较简单，遍历所有的 BeanPostProcessors，找到类型为 InstantiationAwareBeanPostProcessor 的（这里就是我们实现的 MyInstantiationAwareBeanPostProcess 类），执行 postProcessAfterInstantiation 方法。
```java
if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				if (bp instanceof InstantiationAwareBeanPostProcessor) {
					InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
					if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
						return;
					}
				}
			}
		}
```

如果返回结果为 false，程序直接 return，后面的操作（属性赋值）不会执行，当然这个接口默认返回 true 。
postProcessAfterInstantiation 方法可以用来判断这个 Bean 是否要进行属性赋值（populateBean），如果不需要可以进行拦截返回 false。


## Spring Bean 属性赋值前阶段
Bean 属性值元信息 ： PropertyValues
Bean 属性赋值前回调 ：

> Spring 1.2 - 5.0
> org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessPropertyValues

> Spring 5.1
> org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessProperties


定义bean:

```java
<bean id="userHolder" class="com.spring.ioc.domain.UserHolder"
      autowire="constructor">
    <property name="description" value="The user holder" />
</bean>
```

实现postProcessProperties方法：

```java
static class MyInstantiationAwareBeanPostProcess implements InstantiationAwareBeanPostProcessor {
   ....
    //这里的user,是不会执行该回调的，这是因为在上面postProcessAfterInstantiation中拦截，返回false,
    //所以将阻止在user实例上调用任何后续的InstantiationAwareBeanPostProcessor实例。(postProcessProperties就是)

    //而superUser更是在postProcessBeforeInstantiation方法进行了操作，返回的是一个代理bean,更是完全跳过实例化阶段。
    
    //所以这两个bean 无法执行属性赋值前操作，需要一个新的userHolder来进行回调。
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (ObjectUtils.nullSafeEquals(beanName, "userHolder") && UserHolder.class.equals(bean.getClass())) {
            final MutablePropertyValues propertyValues;
            // 兼容 pvs 为空的情况 pvs 对应xml中配置的 <property .../>
            if (pvs instanceof MutablePropertyValues) {
                propertyValues = (MutablePropertyValues) pvs;
            } else {
                propertyValues = new MutablePropertyValues();
            }
            if(propertyValues.contains("description")){
                // PropertyValue 无法直接覆盖，因为是 final 类型
                PropertyValue name = propertyValues.getPropertyValue("description");
                propertyValues.removePropertyValue("description");
                propertyValues.addPropertyValue("description","binbinshan v2");
            }
            return propertyValues;
        }
        return null;
    }
}
```

打印 beanFactory.getBean("userHolder", UserHolder.class); 

```java
UserHolder{user=SuperUser{address='BEIJING'} User{id=null, name='binbinshan-b', localResource=null, city=null, birthCity=null, lifeCity=null, workCity=null}, description='binbinshan v2'}
```

可以发现 UserHolder中 description 由 "The user holder"  变为了 "binbinshan v2"。

### 源码

在AbstractAutowireCapableBeanFactory#doCreateBean方法中会调用populateBean，进行bean 填充，而populateBean方法会获取到 description 字段和值 The user holder。

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16289460869833.jpg)


而在ibp.postProcessProperties()，则会调用我们实现的 postProcessProperties方法，去进行赋值。执行完之后，description 字段的值从"The user holder" 变为了 "binbinshan v2"

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16289462590167.jpg)

最后通过 applyPropertyValues 方法将最新的属性设置到 bw（BeanWrapper）对象中:

![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16289464590938.jpg)




## Spring Bean 属性赋值阶段

属性前赋值的最后一步applyPropertyValues 其实就是属性赋值阶段的代码，进入方法：

这里主要做了三步：
1. 遍历我们前面传进来的 pvs 参数
2. 进行 deepCopy 深拷贝
3. 然后通过 bw.setPropertyValues(new MutablePropertyValues(deepCopy)); 进行赋值操作


![](https://github.com/binbinshan/thinking-in-spring/blob/master/images/16289467541696.jpg)


