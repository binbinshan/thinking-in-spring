package com.spring.ioc.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, InitializingBean
, SmartInitializingSingleton , DisposableBean {

    private User user;

    private String description;

    private ClassLoader classLoader;

    private BeanFactory beanFactory;

    private String beanName;

    private Environment environment;
    public UserHolder() {}
    public UserHolder(User user) {
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserHolder{" +
                "user=" + user +
                ", description='" + description + '\'' +
                '}';
    }

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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init(){
        this.description = "The userHolder v4";
        System.out.println("初始化阶段 : @PostConstruct -> The user holder v4");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.description = "The userHolder v5";
        System.out.println("初始化阶段 : afterPropertiesSet() -> The user holder v5");
    }

    public void customInit() throws Exception {
        this.description = "The userHolder v6";
        System.out.println("初始化阶段 : customInit() -> The user holder v6");
    }


    @Override
    public void afterSingletonsInstantiated() {
        this.description = "The userHolder v8";
        System.out.println("初始化完成阶段 : afterSingletonsInstantiated() -> The user holder v8");
    }

    @PreDestroy
    public void preDestroy(){
        this.description = "The userHolder v10";
        System.out.println("销毁阶段 : @PreDestroy -> The user holder v10");
    }

    @Override
    public void destroy() throws Exception {
        this.description = "The userHolder v11";
        System.out.println("销毁阶段 : DisposableBean#destroy -> The user holder v11");
    }

    public void customDestroy(){
        this.description = "The userHolder v12";
        System.out.println("销毁阶段 : customDestroy -> The user holder v12");
    }

    @Override
    public void finalize() throws Throwable {
        System.out.println(beanName + " 被垃圾回收");
    }

}
