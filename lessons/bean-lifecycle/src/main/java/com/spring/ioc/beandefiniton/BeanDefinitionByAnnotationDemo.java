package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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


