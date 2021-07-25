# Spring Framework总览

<!-- GFM-TOC -->
* [Spring Framework总览](#Spring-Framework总览)
  * [1.Spring特性总览](#1.Spring特性总览)
  * [2.Spring版本](#2.Spring版本)
  * [3.Spring模块化设计](#3.Spring模块化设计)
  * [4.Spring对Java语言特性运用](#4.Spring对Java语言特性运用)
  * [5.Spring对JDK的API实践](#5.Spring对JDK的API实践)
  * [6.Spring对JavaEE的API整合](#6.Spring对JavaEE的API整合)
  * [7.Spring编程模型](#7.Spring编程模型)
  * [8.Spring核心价值](#8.Spring设计模式)
<!-- GFM-TOC -->

> 工具版本：
> Spring Framework 5.2.2.RELEASE
> JDK8

## 1.Spring特性总览

> Spring Framework中有很多特性，分为五大类

核心特性(Core)

> IoC容器（IoC Container）、Spring事件（Events）、资源管理（Resources）、国际化（i18n）、校验（Validation）、数据绑定（Data Binding）、类型装换（Type Conversion）、Spring表达式（Spring Express Language）SpEL、面向切面编程（AOP）

测试特性(Testing)

> 模拟对象（Mock Objects）、TestContext框架（TestContext Framework）、SpringMVC测试（Spring MVC Test）、Web 测试客户端（WebTestClient）

数据访问特性(Data Access)

> JDBC、事务抽象（Transactions）、DAO支持（DAO Support）、O/R映射（O/R Mapping）、XML 编列（XML Marshalling）


web技术特性(Web)

> Web Servlet 技术栈
>> Spring MVC、WebSocket、SockJS

> Web Reactive 技术栈
>> Spring WebFlux、WebClient、WebSocket

技术集成特性(Integration)

> 远程调用（Remoting）、Java消息服务（JMS）、Java连接架构（JCA）、Java管理扩展（JMX）、Java邮件客户端（Email）、本地任务（Tasks）、本地调度（Scheduling）、缓存抽象（Caching）、Spring测试（Testing）


## 2.Spring版本

* Spring Framework 5.3.x 支持 JDK 8-19

* Spring Framework 5.2.x 支持 JDK 8-15

* Spring Framework 5.1.x 支持 JDK 8-12

* Spring Framework 5.0.x 支持 JDK 8-10

* Spring Framework 4.3.x 支持 JDK 6-8


## 3.Spring模块化设计

Spring是按照不同模块进行设计的，其中包括但不限于有以下模块：

• spring-aop

• spring-aspects

• spring-context-indexer

• spring-context-support

• spring-context

• spring-core

• spring-expression

• spring-instrument

• spring-jcl

• spring-jdbc

• spring-jms

• spring-messaging

• spring-orm

• spring-oxm

• spring-test

• spring-tx

• spring-web

• spring-webflux

• spring-webmvc

• spring-websocket



## 4.Spring对Java语言特性运用

| 语言特性           | Spring支持版本 | Spring代表实现 |
|----------------|------------|------------|
| 注解（Annotation） | 1.2 +      |@Transactional|
|枚举（Enumeration）|1.2 +|Propagation|
|for-each 语法|3.0 +|AbstractApplicationContext|
|自动装箱（AutoBoxing）|3.0 +|            |
|泛型（Generic）|3.0 +|ApplicationListener|
|接口 @Override|4.0 +|            |
|Diamond 语法|5.0 +|DefaultListableBeanFactory|
|try-with-resources 语法|5.0 +|ResourceBundleMessageSource|
|Lambda 语法|5.0 +|PropertyEditorRegistrySupport|

## 5.Spring对JDK的API实践

| API 类型          | Spring支持版本 | Spring代表实现 |
|----------------|------------|------------|
|反射（Reflection）|1.0 +|MethodMatcher|
|Java Beans|1.0 +|CachedIntrospectionResults|
|动态代理（Dynamic Proxy）|1.0 +|JdkDynamicAopProxy|
|XML 处理（DOM,SAX...）|1.0 +|XmlBeanDefinitionReader|
|Java 管理扩展（JMX）|1.2 +|@ManagedResource|
|Instrumentation|2.0 +|InstrumentationSavingAgent|
|并发框架（J.U.C）|3.0 +|ThreadPoolTaskScheduler|
|格式化（Formatter）|3.0 +|DateFormatter|
|JDBC 4.0（JSR 221）|1.0 +|JdbcTemplate|
|Common Annotations（JSR 250 ）|2.5 +|CommonAnnotationBeanPostProcessor|
|可插拔注解处理 API（JSR 269）|5.0 +|@Indexed|
|Java Compiler API（JSR 199）|5.0 +|TestCompiler（单元测试）|
|Fork/Join 框架（JSR 166）|3.1 +|ForkJoinPoolFactoryBean|
|NIO 2（JSR 203）|4.0 +|PathResource|
|可重复 Annotations（JSR 337）|4.0 +|@PropertySources|
| Stream API（JSR 335）| 4.2 +| StreamConverter|
|CompletableFuture（J.U.C） | 4.2 +| CompletableToListenableFutureAdapter|

## 6.Spring对JavaEE的API整合
> JavaEE是一组建立在JavaSE(JDK)之上的标准，解决企业级开发中的一系列问题。它仅仅是个标准，是对一系列接口的约定，众多厂商围绕这个标准做实现。如tomcat、JBoss，WebSphere等。
> 常见标准：Servlet、EJB、JPA、JTA、JavaMail

Java EE 数据存储相关

| JSR 规范 | Spring 支持版本  | Spring代表实现  |
|---|---|---|
|JTA(JSR 907)|1.0 +|JtaTransactionManager|
|JPA(EJB 3.0 JSR 220的成员)|2.0 +|JpaTransactionManager|
|Java Caching API(JSR 107)|3.2 +|JCacheCache|

Java EE Bean 技术相关

| JSR 规范 | Spring 支持版本  | Spring代表实现  |
|---|---|---|
|JMS(JSR 914)|1.1 +|JmsTemplate|
|EJB 2.0 (JSR 19)|1.0 +|AbstractStatefulSessionBean|
|Dependency Injection for Java(JSR 330)|2.5 +|AutowiredAnnotationBeanPostProcessor|
|Bean Validation(JSR 303)|3.0 +|LocalValidatorFactoryBean|

## 7.Spring编程模型

Spring中的编程模型有5种：

##### 面向对象编程

契约接口：Aware、BeanPostProcessor ...

设计模式：观察者模式、组合模式、模板模式 ...

对象继承：Abstract* 类


##### 面向切面编程

动态代理：JdkDynamicAopProxy

字节码提升：ASM、CGLib、AspectJ...

##### 面向元编程

注解：模式注解（@Component、@Service、@Respository ...）

配置：Environment抽象、PropertySources、BeanDefinition ...

泛型：GenericTypeResolver、ResolvableType ...


##### 函数驱动

函数接口：ApplicationEventPublisher

Reactive：Spring WebFlux


##### 模块驱动

Maven Artifacts

Java 9 Automatic Modules

Spring @Enable*

## 8.Spring设计模式

除了传统的GOF 23外，Spring还具有自己独特的设计模式

* 前缀模式
  * Enable模式
  * Configurable模式

* 后缀模式
  * 处理器模式
    * Processor
    * Resolver
    * Handler
  * 意识模式
    * Aware
  * 配置器模式
    * Configuror
  * 选择器模式
    * org.springframework.context.annotation.ImportSelector



最后用张图来说明Spring的核心特性

<div align="center"> <img src="https://github.com/binbinshan/thinking-in-spring/blob/master/images/Spring%20%E6%A0%B8%E5%BF%83%E7%89%B9%E6%80%A7.png" width="1200px"> </div><br>

