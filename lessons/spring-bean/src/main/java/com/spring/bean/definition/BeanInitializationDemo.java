package com.spring.bean.definition;

import com.spring.bean.definition.factory.DefaultUserFactory;
import com.spring.bean.definition.factory.UserFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * bean 初始化
 * @author shanbin
 */

public class BeanInitializationDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(BeanInitializationDemo.class);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 非延迟初始化在 Spring 应用上下文启动完成后，被初始化
        System.out.println("Spring 应用上下文已启动...");
        //获取BeanDefinition的元信息
        boolean isLazy = applicationContext.getBeanDefinition("userFactory").isLazyInit();
        System.out.println("是否延迟加载 ：" + isLazy);
        // 依赖查找 UserFactory
        UserFactory userFactory = (UserFactory) applicationContext.getBean("userFactory");
        System.out.println(userFactory);
        System.out.println("Spring 应用上下文准备关闭...");
        // 关闭 Spring 应用上下文
        applicationContext.close();
        System.out.println("Spring 应用上下文已关闭...");
    }

    @Bean(initMethod = "initUserFactory",destroyMethod ="destroyUserFactory")
    @Lazy
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }
}
