package spring.dependency.lookup;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 单一类型的延迟依赖查找
 * @author shanbin
 */
public class SingleBeanProviderDemo {
    public static void main(String[] args) {
        //通过注解上下文构建IOC容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        //注册 SingleBeanProviderDemo
        applicationContext.register(SingleBeanProviderDemo.class);
        //调用register() 需要手动refresh()
        applicationContext.refresh();

        lookupByObjectProvider(applicationContext);
        lookupIfAvailable(applicationContext);
        lookupByStreamOps(applicationContext);

    }

    @Bean
    @Primary
    public String helloWorld() { // 方法名就是 Bean 名称 = "helloWorld"
        return "Hello,World";
    }

    @Bean
    public String message() {
        return "Message";
    }

    private static void lookupByObjectProvider(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
        System.out.println(objectProvider.getObject());
    }

    private static void lookupByStreamOps(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
        // Stream -> Method reference
        objectProvider.stream().forEach(System.out::println);
    }

    private static void lookupIfAvailable(AnnotationConfigApplicationContext applicationContext) {
        //ObjectProvider<User> userObjectProvider = applicationContext.getBeanProvider(User.class);
        //User user = userObjectProvider.getIfAvailable(User::createUser);
        //System.out.println("当前 User 对象：" + user);
    }
}
