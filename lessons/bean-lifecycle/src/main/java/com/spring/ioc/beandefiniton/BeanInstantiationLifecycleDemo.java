package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.UserHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanInstantiationLifecycleDemo {

    public static void main(String[] args) throws InterruptedException {
        executeBeanFactory();
        //System.out.println("-------------------------分隔符-----------------------");
        //executeApplicationContext();
    }

    private static void executeApplicationContext() {
        String[] locations = {"classpath:/META-INF/dependency-lookup-context.xml"};

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(locations);

        //第一种添加 BeanpostBeanProcess 实现方式：
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        //第二种添加 BeanpostBeanProcess 实现方式：
        //将 <bean class="com.huajie.thinking.in.spring.bean.lifecycle.MyInstantiationAwareBeanPostProcess"/> 配置在 xml 中

        applicationContext.refresh();

        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);

        System.out.println(userHolder);

        applicationContext.close();

    }

    private static void executeBeanFactory() throws InterruptedException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());
        beanFactory.addBeanPostProcessor(new MyDestructionAwareBeanPostProcessor());
        //添加 CommonAnnotationBeanPostProcessor 触发 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);
        beanFactory.preInstantiateSingletons();
        //User user = beanFactory.getBean("user", User.class);
        //SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        //System.out.println(user);
        //System.out.println(superUser);

        //beanFactory.destroyBean("userHolder",userHolder);
        beanFactory.destroySingletons();
        System.out.println(userHolder);

        userHolder = null;
        System.gc();
        Thread.sleep(5000);
    }

}
