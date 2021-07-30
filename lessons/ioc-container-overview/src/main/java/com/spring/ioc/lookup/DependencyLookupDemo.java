package com.spring.ioc.lookup;

import com.spring.ioc.annotation.SuperUserAnnotation;
import com.spring.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 *
 * 使用spring Ioc容器进行 依赖查找
 *
 * @author shanbin
 */
public class DependencyLookupDemo {

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("META-INF/dependency-lookup-context.xml");

        //lookupByName(beanFactory);
        //lookupByType(beanFactory);
        lookupByAnnotation(beanFactory);
        //lookupCollectionByType(beanFactory);
    }

    /**
     * 根据 注解 进行查找
     * @param beanFactory
     */
    private static void lookupByAnnotation(BeanFactory beanFactory){

        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = (Map) listableBeanFactory.getBeansWithAnnotation(SuperUserAnnotation.class);
            System.out.println("根据注解查找 SuperUser 集合对象：" + users);
        }
    }

    /**
     * 根据bean name查找
     * @param beanFactory
     */
    private static void lookupByName(BeanFactory beanFactory){
        User user = (User) beanFactory.getBean("user");
        System.out.println("根据Bean name 查找：" + user);
    }

    /**
     * 根据bean type查找
     * @param beanFactory
     */
    private static void lookupByType(BeanFactory beanFactory){
        User bean = beanFactory.getBean(User.class);
        System.out.println("根据Bean type 查找：" + bean);
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找到的所有的 User 集合对象：" + users);
        }
    }
}
