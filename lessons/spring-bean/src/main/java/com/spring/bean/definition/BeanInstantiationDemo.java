package com.spring.bean.definition;

import com.spring.ioc.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * bean 实例化
 * @author shanbin
 */
public class BeanInstantiationDemo {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/bean-instantiation-context.xml");
        User constructorArgUser = (User) applicationContext.getBean("constructor-arg-user");
        User staticMethodUser = (User) applicationContext.getBean("static-method-user");
        User beanFactoryUser = (User) applicationContext.getBean("bean-factory-user");
        User factoryBeanUser = (User) applicationContext.getBean("factory-bean-user");

        System.out.println(constructorArgUser);
        System.out.println(staticMethodUser);
        System.out.println(beanFactoryUser);
        System.out.println(factoryBeanUser);
    }

}
