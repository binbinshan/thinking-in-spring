package spring.dependency.injection;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class SpringBeanDefinitionRegisterSourceDemo {




    @Bean
    private User user(){
        User user = new User();
        user.setId(1L);
        user.setName("1");
        return user;
    }

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(SpringBeanDefinitionRegisterSourceDemo.class);


        // 启动 Spring 应用上下文
        applicationContext.refresh();

        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);
        definitionBuilder.addConstructorArgReference("user");
        applicationContext.registerBeanDefinition("userHolder", definitionBuilder.getBeanDefinition());


        // 依赖查找并且创建 Bean
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();

    }
}
