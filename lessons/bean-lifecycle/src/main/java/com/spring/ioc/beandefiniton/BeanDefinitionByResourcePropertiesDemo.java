package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.util.Map;

/**
 * @author shanbin
 */
public class BeanDefinitionByResourcePropertiesDemo {

    public static void main(String[] args) {
        //创建 BeanFactory 容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //使用 PropertiesBeanDefinitionReader 读取 BeanDefinition
        PropertiesBeanDefinitionReader propertiesBeanDefinitionReader = new PropertiesBeanDefinitionReader(beanFactory);
        //指定EncodedResource ，否则可能出现乱码
        Resource resource = new ClassPathResource("META-INF/default.properties");
        EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
        //Load bean definitions
        propertiesBeanDefinitionReader.loadBeanDefinitions(encodedResource);

        lookupCollectionByType(beanFactory);
    }

    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listBeanFactory.getBeansOfType(User.class);
            System.out.println("查找到的所有集合对象---" + users);
        }
    }
}
