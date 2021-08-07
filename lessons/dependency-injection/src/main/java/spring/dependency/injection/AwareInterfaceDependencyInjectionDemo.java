package spring.dependency.injection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 接口回调的方式
 * @author shanbin
 */
public class AwareInterfaceDependencyInjectionDemo implements BeanFactoryAware, ApplicationContextAware, BeanNameAware {

    private static BeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    private static String beanName;

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类） -> Spring Bean
        context.register(AwareInterfaceDependencyInjectionDemo.class);
        // 启动 Spring 应用上下文
        context.refresh();
        System.out.println(beanFactory == context.getBeanFactory());
        System.out.println(applicationContext == context);
        System.out.println(beanName);
        // 显示地关闭 Spring 应用上下文
        context.close();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        AwareInterfaceDependencyInjectionDemo.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AwareInterfaceDependencyInjectionDemo.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
