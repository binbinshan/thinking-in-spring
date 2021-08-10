package spring.dependency.injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ResolvableDependencySourceDemo {
    @Autowired
    private String name;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo.class);
        //把当前类 bean 加入到容器中
        applicationContext.refresh();

        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
            configurableListableBeanFactory.registerResolvableDependency(String.class, "binbinshan");
        }
        ResolvableDependencySourceDemo bean = applicationContext.getBean(ResolvableDependencySourceDemo.class);
        System.out.println(bean.name);
        applicationContext.getBean(String.class);
        applicationContext.close();
    }
}
