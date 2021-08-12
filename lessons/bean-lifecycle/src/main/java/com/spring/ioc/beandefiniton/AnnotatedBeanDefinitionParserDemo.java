package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
