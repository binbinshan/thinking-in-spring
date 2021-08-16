package spring.configuration.metadata;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class BeanConfigurationMetadataDemo {


    public static void main(String[] args) {
        // BeanDefinition 定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);

        beanDefinitionBuilder.addPropertyValue("name","binbinshan");
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        // 附加属性
        beanDefinition.setAttribute("aaa","zhangsan");
        //当前 BeanDefinition 来自于何方（辅助作用）
        beanDefinition.setSource(BeanConfigurationMetadataDemo.class);

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("user",beanDefinition);
        User user = beanFactory.getBean("user", User.class);
        Object name = beanDefinition.getAttribute("aaa");
        System.out.println(name);
        System.out.println(user);
        System.out.println(beanDefinition.getSource());
    }
}
