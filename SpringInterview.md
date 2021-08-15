# Spring 面试点
- [Spring 面试点](#spring-面试点)
    - [1.什么是 Spring Framework？](#1什么是-spring-framework)
    - [2.Spring Framework 重要模块有哪些？](#2spring-framework-重要模块有哪些)
    - [3.什么是 IoC ？](#3什么是-ioc-)
    - [4.依赖查找和依赖注入的区别？](#4依赖查找和依赖注入的区别)
    - [5.什么是 Spring IoC 容器？](#5什么是-spring-ioc-容器)
    - [6.BeanFactory 与 FactoryBean？](#6beanfactory-与-factorybean)
    - [7.什么是 Spring BeanDefinition？](#7什么是-spring-beandefinition)
    - [8. 如何构建BeanDefinition？](#8-如何构建beandefinition)
    - [9.如何注册一个 Spring Bean？](#9如何注册一个-spring-bean)
    - [10.有多少种依赖注入的方式？](#10有多少种依赖注入的方式)
    - [11.构造器注入还是 Setter 注入？](#11构造器注入还是-setter-注入)
    - [12.注入和查找的依赖来源是否相同？](#12注入和查找的依赖来源是否相同)
    - [13.单例对象能在 IoC 容器启动后注册吗？](#13单例对象能在-ioc-容器启动后注册吗)
    - [14.Spring 依赖注入的来源有哪些？](#14spring-依赖注入的来源有哪些)
    - [15.Spring 內建的 Bean 作用域有几种？](#15spring-內建的-bean-作用域有几种)
    - [16.singleton Bean 是否在一个应用是唯一的？](#16singleton-bean-是否在一个应用是唯一的)
    - [17.作用域 “application”Bean 是否被其他方案替代](#17作用域-applicationbean-是否被其他方案替代)
    - [18.BeanPostProcessor 的使用场景有哪些？](#18beanpostprocessor-的使用场景有哪些)
    - [19.BeanFactoryPostProcessor 与 BeanPostProcessor 的区别？](#19beanfactorypostprocessor-与-beanpostprocessor-的区别)
    - [20.BeanFactory 是怎样处理 Bean 生命周期？](#20beanfactory-是怎样处理-bean-生命周期)


### 1.什么是 Spring Framework？
Spring使得创建JavaEE应用程序变的简单，它是为了解决企业应用开发的复杂性而创建的。并根据应用程序的需要灵活地创建多种体系结构。如IOC、AOP等。

------ 

### 2.Spring Framework 重要模块有哪些？
* spring-core：Spring 基础 API 模块，如资源管理，泛型处理
* spring-beans：Spring Bean 相关，如依赖查找，依赖注入
* spring-aop : Spring AOP 处理，如动态代理，AOP 字节码提升
* spring-context : 事件驱动、注解驱动，模块驱动等
* spring-expression：Spring 表达式语言模块
 
------ 

### 3.什么是 IoC ？
控制反转(IoC)是一种编程原理，IoC是反转控制，类似于好莱坞原则，主要有依赖查找和依赖注入实现。Ioc意味着将你设计好的对象交给容器控制，而不是传统的在你的对象内部直接控制。

------ 

### 4.依赖查找和依赖注入的区别？
依赖查找是一个主动获取的过程，例如需要某个Bean，通过BeanFactory的getBean方法来获取； 

依赖注入是一个被动接受的过程，例如需要某个Bean，我只需在类中方法或字段上添加@Autowired注解即可，由IoC容器来帮我完成查找并注入。

------ 

### 5.什么是 Spring IoC 容器？
Spring框架实现的反转控制(IoC)原理。IoC也称为依赖注入(DI)。在此过程中，对象仅通过构造函数参数、工厂方法参数或在对象实例被构造或从工厂方法返回后设置的属性来定义它们的依赖项(即它们使用的其他对象)。然后容器在创建bean时注入这些依赖项。

------ 

### 6.BeanFactory 与 FactoryBean？
BeanFactory 是 IoC 底层容器，而ApplicationContext 是 BeanFactory 的子接口。BeanFactory提供了IOC配置框架和基本功能，而ApplicationContext添加了更多企业特定的功能。ApplicationContext是对一个BeanFactory完整的超集。

FactoryBean 是 创建 Bean 的一种方式，帮助实现复杂的初始化逻辑。通过实现FactoryBean接口，并重写getObject()。

------ 

### 7.什么是 Spring BeanDefinition？
Spring Bean 是通过 BeanDefinition 进行定义的，BeanDefinition 是 Spring 中定义Bean的配置元信息的接口。
BeanDefinition中包括Bean的一些元信息：如 Bean 名称、作用域、生命周期回调、 Bean 属性等。

------ 

### 8. 如何构建BeanDefinition？
在spring中有两种方式构建BeanDefinition：

1. 通过 BeanDefinitionBuilder 构建
2. 通过 AbstractBeanDefinition 以及派生类

------ 

### 9.如何注册一个 Spring Bean？
通过 BeanDefinition 进行注册。


------ 

### 10.有多少种依赖注入的方式？

构造器注入 、Setter注入 、字段注入、方法注入、接口回调注入

------ 

### 11.构造器注入还是 Setter 注入？

两种依赖注入的方式均可使用，如果是必须依赖的话，那么推荐使用构 造器注入，Setter 注入用于可选依赖。

------ 

### 12.注入和查找的依赖来源是否相同？

否，依赖查找的来源仅限于 Spring BeanDefinition 以及单例对象，而依赖注入的来源还包括 Resolvable Dependency 以及 @Value 所标注的外部化配置。

------ 

### 13.单例对象能在 IoC 容器启动后注册吗？
可以的，单例对象的注册与 BeanDefinition 不同，BeanDefinition 会被 ConfigurableListableBeanFactory#freezeConfiguration() 方法影响， 从而冻结注册，单例对象则没有这个限制。

------ 

### 14.Spring 依赖注入的来源有哪些？
Spring BeanDefinition 、单例对象 、 ResolvableDependency 、@Value 外部化配置

------ 

### 15.Spring 內建的 Bean 作用域有几种？
singleton、prototype、request、session、application 以及 websocket

------ 

### 16.singleton Bean 是否在一个应用是唯一的？
否，singleton bean 仅在当前 Spring IoC 容器（BeanFactory）中是 单例对象。

------ 

### 17.作用域 “application”Bean 是否被其他方案替代
可以的，实际上，“application” Bean 与“singleton” Bean 没有 本质区别


------ 

### 18.BeanPostProcessor 的使用场景有哪些？
BeanPostProcessor 提供 Spring Bean 初始化前和初始化后的生命周期回调，分别对应 postProcessBeforelnitialization 以及 pistProcessAfterInitialization 方法，允许对相关的 Bean 进行扩展，甚至是替换。

BeanPostProcessor 的子类 DestructionAwareBeanPostProcessor 提供销毁前的生命周期回调。
BeanPostProcessor 的子类 InstantiationAwareBeanPostProcessor 提供实例化前postProcessBeforeInstantiation，实例化后 postProcessAfterInstantiation，属性赋值前 postProcessProperties 的生命周期回调。

 ApplicationContext 相关的 Aware 回调也是基于 BeanPostProcessor 实现，即 ApplicationContextAwareProcessor

------ 

### 19.BeanFactoryPostProcessor 与 BeanPostProcessor 的区别？
两者无法进行对比，BeanFactoryPostProcessor 是 Spring BeanFactory（实际是ConfigurableListableBeanFactory）的后置处理器，用于扩展 BeanFactory，或者通过 BeanFactory 进行依赖查找和依赖注入。
而 BeanPostProcessor 则是直接与 BeanFactory 关联，属于 N 对 1 的关系。

BeanFactoryPostProcessor 必须有 Spring ApplicationContext 执行，BeanFactory 无法直接与其交互。

------ 

### 20.BeanFactory 是怎样处理 Bean 生命周期？
BeanFactory 的默认实现为 DefaultListableBeanFactory ,其中 Bean 生命周期与方法映射如下：

BeanDefinition 注册阶段 - registerBeanDefinition

BeanDefinition 合并阶段 - getMergedBeanDefinition

Bean 实例化前阶段 - resolveBeforeInstantiation

Bean 实例化阶段 - createBeanInstance

Bean 实例化后阶段 - populateBean

Bean 属性赋值前阶段 - populateBean

Bean 属性赋值阶段 - populateBean

Bean Aware 接口回调阶段 - initializeBean

Bean 初始化前阶段 - initializeBean

Bean 初始化阶段 - initializeBean

Bean 初始化后阶段 - initializeBean

Bean 初始化完成阶段 - preInstantiateSingletons

Bean 销毁前阶段 - destroyBean

Bean 销毁阶段 - destroyBean
