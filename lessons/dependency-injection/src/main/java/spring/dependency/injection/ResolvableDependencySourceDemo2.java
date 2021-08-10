package spring.dependency.injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ResolvableDependencySourceDemo2 {
    @Autowired
    private String name;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ResolvableDependencySourceDemo2.class);
        //第二种方式
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            beanFactory.registerResolvableDependency(String.class,"binbinshan");
        });
        applicationContext.refresh();
        ResolvableDependencySourceDemo2 bean = applicationContext.getBean(ResolvableDependencySourceDemo2.class);
        System.out.println(bean.name);
        applicationContext.close();
    }
}
