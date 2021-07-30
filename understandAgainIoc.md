# 再次理解IOC

- [再次理解IOC](#再次理解ioc)
  - [1.IoC 发展简介](#1ioc-发展简介)
    - [什么是IOC?](#什么是ioc)
    - [IOC发展历程](#ioc发展历程)
  - [2.IoC 主要实现策略](#2ioc-主要实现策略)
    - [依赖查找](#依赖查找)
    - [依赖注入](#依赖注入)
  - [3.IoC 容器的职责](#3ioc-容器的职责)
  - [4.IOC的主要实现](#4ioc的主要实现)
  - [5.轻量级IOC容器有哪些特征与好处？](#5轻量级ioc容器有哪些特征与好处)
  - [6.依赖注入和依赖查找](#6依赖注入和依赖查找)
  - [7.set注入和构造器注入](#7set注入和构造器注入)
    - [Spring官网上对于set注入和构造器注入的阐述](#spring官网上对于set注入和构造器注入的阐述)

## 1.IoC 发展简介

### 什么是IOC?

在软件工程中，控制反转(IoC)是一种**编程原理**。IoC将控制流与传统控制流进行反转。在IoC中，计算机程序的自定义编写部分接收来自通用框架的控制流。与传统的过程式编程相比，在传统编程中，自定义代码调用进入可重用的库以处理通用任务，但通过控制反转，调用自定义或特定于任务的代码的是框架。

通俗来讲，Ioc意味着将你设计好的对象交给容器控制，而不是传统的在你的对象内部直接控制。

### IOC发展历程

* 1983年，Richard E. Sweet 在《The Mesa Programming Environment》中提出“Hollywood Principle”（好莱坞原则）

* 1988年，Ralph E. Johnson & Brian Foote 在《Designing Reusable Classes》中提出“Inversion of control”（控制反转）

* 1996年，Michael Mattsson 在《Object-Oriented Frameworks, A survey of methodological issues》中将“Inversion of control”命名为 “Hollywood principle”

* 2004年，Martin Fowler 在《Inversion of Control Containers and the Dependency Injection pattern》中提出了自己对 IoC 以及 DI 的理解

* 2005年，Martin Fowler 在 《InversionOfControl》对 IoC 做出进一步的说明

## 2.IoC 主要实现策略

在面向对象编程中，有几种基本技术可以实现控制反转。这些是：

* 使用服务定位器模式
> 这种模式是JavaEE里所定义的一种模式，通常使用JNDI技术获取JavaEE组件，例如获取EJB、DataSource等

* 使用依赖注入
    * 构造函数注入
    * 参数注入
    * setter注入
    * 接口注入

* 使用上下文查找
> 例如Java中的Java Beans技术，通过BeanContext来传输Bean和管理Bean的层次性。

* 使用模板方法设计模式
> 例如Spring JDBC中JDBC Template，返回一种类似于Statement的Callback，实现接口时不需要关心Callback从哪里来，也是一种控制反转的行为。

* 使用策略设计模式


在《Expert One-on-One™ J2EE™ Development without EJB™》提到的主要实现策略有两种，分别是依赖查找和依赖注入。

### 依赖查找
在传统JavaEE中通常来说实现的是依赖查找而非依赖注入，比如EJB、Apache Avalon。
依赖查找是指容器会提供一种回调的机制到组件，然后通过上下文查询的方式就能获取到这个组件。

### 依赖注入
组件不需要进行查找，通常是容器帮我们自动注入或者是我们手动注入，依赖完全是由容器来负责，将解析的对象传递给JavaBean属性或构造函数。也就是Setter注入和构造函数注入两种方式传递。在Spring中有API、Java注解、XML方式来实现依赖注入。


## 3.IoC 容器的职责
通用职责

* 依赖处理
    * 依赖查找
    * 依赖注入
* 生命周期的管理
    * 容器
    * 托管的资源（Java Beans 或其他资源）
* 配置
    * 容器
    * 外部化配置
    * 托管的资源（Java Beans 或其他资源）


## 4.IOC的主要实现
主要实现

* Java SE

    * Java Beans
    * Java ServiceLoader SPI
    * JNDI（Java Naming and Directory Interface）

* Java EE

    * EJB（Enterprise Java Beans）
    * Servlet

* 开源
    * Apache Avalon（http://avalon.apache.org/closed.html）
    * PicoContainer（http://picocontainer.com/）
    * Google Guice（https://github.com/google/guice）
    * Spring Framework（https://spring.io/projects/spring-framework）

## 5.轻量级IOC容器有哪些特征与好处？

轻量级IOC容器有以下几个特征：
1. 可以管理应用程序代码的容器，是指容器可以管理代码的运行。
2. 可以快速启动。
3. 容器不需要任何特殊配置即可在其中部署对象。（EJB的容器在部署的时候就需要大量的xml配置）。
4. 占用空间很小，APl依赖性最小，可以在各种环境中运行。（比如EJB、servlet中大量的API需要实现，使用）。
5. 部署工作和性能开销方面非常低，并且可以部署和管理细粒度对象以及粗粒度组件。

轻量级IOC容器有以下几个好处：

1. 避免了整体容器。(执行层面和管理层面不应放在一起)。
2. 最大化代码重用性。
3. 更大程度上的面向对象。（EJB并不是一个很好的面向对象）。
4. 提高生产力。（EJB 或者说 Java EE的过于庞大，导致产品有效性或效率性是不够的）。
5. 更好的可测试性。（Spring 推崇的是JUnit 单元测试和集成测试）。


## 6.依赖注入和依赖查找

依赖查找是一个主动获取的过程，例如需要某个Bean，通过BeanFactory的getBean方法来获取；
依赖注入是一个被动接受的过程，例如需要某个Bean，我只需在类中方法或字段上添加@Autowired注解即可，由IoC容器来帮我完成查找并注入。

两者的区别：

| 类型 | 依赖处理 | 实现便利性 | 代码侵入性 | API依赖性 | 可读性 |
|----|------|-------|-------|--------|-----|
|依赖查找|主动获取|相对繁琐|侵入业务逻辑|依赖容器API|良好|
|依赖注入|被动提供|相对便利|低侵入性|不依赖容器API|一般|


## 7.set注入和构造器注入

### Spring官网上对于set注入和构造器注入的阐述

Spring团队通常**提倡构造函数注入**，因为它允许您将应用程序组件实现为**不可变对象**，并确保所需的依赖项不为空。而且，构造函数注入的组件总是以完全初始化的状态返回给客户机(调用)代码。顺便提一下，**大量的构造函数参数是一种糟糕的代码味道**，这意味着类可能有太多的责任，应该重构以更好地处理适当的关注点分离。

**Setter注入主要应该只用于可选依赖项**，这些依赖项可以在类中分配合理的默认值。否则，必须在代码使用依赖项的任何地方执行非空检查。setter注入的一个好处是，**setter方法使该类的对象能够在稍后进行重新配置或重新注入**。

###《Expert One-on-One™ J2EE™ Development without EJB™》中的阐述
这本书的作者是Spring初代作者写的，而在本书比较推崇的是set注入。

> **set注入**的优点：
1. 在ide中很好地支持JavaBean属性。set/get
2. 不需要过多的文档说明。例如getName/setName，一看就知道做什么。
3. 可以使用标准的Java Beans的PropertyEditor 进行类型转换。setAsText 通过解析给定的字符串来设置属性值。
4. 如果每个setter都有相应的getter(使属性既可读又可写)，则可以询问组件当前的配置状态。

> **set注入**的缺点：
1. set注入无法指定注入的顺序。Spring为此提供了org.springframework.beans.factory.InitializingBean接口;它还提供了调用任意init方法的能力

> **构造器注入**的优点：
1. 在可以在任何业务方法中调用它之前，每个托管对象都保证处于一致的全状态配置中。这是构造函数注入的主要动机。保证了注入对象的不变性。


> **构造器注入**的缺点：
尽管多参数构造函数也是java语言的特性，但在现有代码中，多参数构造函数可能没有JavaBean属性的使用常见。
1. Java构造函数参数没有内省可见的名称。
2. 构造函数参数列表在ide中得到的支持不如JavaBean setter方法。
3. 长构造函数参数列表和大构造函数体可能变得笨拙。
4. 与JavaBeans相比，对可选属性的支持较差
5. 当合作者在对象构造中被传递进来时，就不可能改变对象中持有的引用。

