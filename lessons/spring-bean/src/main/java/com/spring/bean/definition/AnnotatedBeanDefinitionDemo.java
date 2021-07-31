package com.spring.bean.definition;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 *
 * @author shanbin
 */
@Import(AnnotatedBeanDefinitionDemo.Config.class)
public class AnnotatedBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationConfigApplicationContext.class);
        applicationContext.refresh();

        // 3.通过 BeanDefinition 注册 API 实现
        // 3.1.命名 Bean 的注册方式
        registerUserBeanDefinition(applicationContext, "binbinshan-user");
        // 3.2. 非命名 Bean 的注册方法
        registerUserBeanDefinition(applicationContext);

        System.out.println("User 类型的所有 Beans" + applicationContext.getBeansOfType(User.class));
        System.out.println("Config 类型的所有 Beans" + applicationContext.getBeansOfType(Config.class));
    }

    private static void registerUserBeanDefinition(AnnotationConfigApplicationContext applicationContext) {
        registerUserBeanDefinition(applicationContext,null);
    }
    private static void registerUserBeanDefinition(AnnotationConfigApplicationContext applicationContext, String beanName) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder
                .addPropertyValue("id", 100L)
                .addPropertyValue("name", "binbinshan");

        // 判断如果 beanName 参数存在时
        if (StringUtils.hasText(beanName)) {
            // 注册 BeanDefinition
            applicationContext.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            // 非命名 Bean 注册方法
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), applicationContext);
        }
    }


    //@Component //定义当前类作为 Spring Bean（组件）
    public static class Config{
        //@Bean(name = {"@bean-user"})
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("binbinshan");
            return user;
        }
    }
}
