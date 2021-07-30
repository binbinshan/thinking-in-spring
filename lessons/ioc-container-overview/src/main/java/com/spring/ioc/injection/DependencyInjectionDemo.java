package com.spring.ioc.injection;

import com.spring.ioc.repository.UserRepository;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * 使用spring Ioc容器进行 依赖注入
 *
 * @author shanbin
 */
public class DependencyInjectionDemo {

    public static void main(String[] args) {
        //BeanFactory applicationContext= new ClassPathXmlApplicationContext("META-INF/dependency-injection-context.xml");
        //UserRepository userRepository = applicationContext.getBean(UserRepository.class);

        //final User user = userRepository.getUser();
        //System.out.println(user);

        //List<User> users = userRepository.getUsers();
        //users.stream().forEach(c -> System.out.println(c.toString()));

        //User user = userRepository.getUser();
        //System.out.println(user);

        //BeanFactory beanFactory = userRepository.getBeanFactory();
        //System.out.println(beanFactory);
        //
        //ObjectFactory objectFactory = userRepository.getObjectFactory();
        //System.out.println(objectFactory);
        //
        ////抛出NoSuchBeanDefinitionException 说明BeanFactory不是bean
        //System.out.println(beanFactory.getBean(BeanFactory.class));


        //Environment environment = applicationContext.getBean(Environment.class);
        //System.out.println("获取 Environment 类型的 Bean：" + environment);


        // ConfigurableApplicationContext <- ApplicationContext <- BeanFactory
        // ConfigurableApplicationContext#getBeanFactory()
        // 这个表达式为什么不会成立
        //System.out.println(userRepository.getBeanFactory() == applicationContext);


        //BeanFactory beanFactory= new ClassPathXmlApplicationContext("META-INF/dependency-injection-context.xml");
        //UserRepository userRepository = beanFactory.getBean(UserRepository.class);
        //System.out.println(userRepository.getBeanFactory() == beanFactory);

        ClassPathXmlApplicationContext applicationContext= new ClassPathXmlApplicationContext("META-INF/dependency-injection-context.xml");
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        //System.out.println(userRepository.getBeanFactory() == applicationContext);
        //System.out.println(applicationContext);
        System.out.println(userRepository.getBeanFactory());
    }

}
