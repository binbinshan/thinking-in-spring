package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * @author shanbin
 */
public class BeanDefinitionByResourceXmlDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器,
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 使用 XmlBeanDefinitionReader 加载配置，参数需要BeanDefinitionRegistry，而DefaultListableBeanFactory 实现了 BeanDefinitionRegistry
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/beanDefinition-context.xml";
        int beanDefinitionsCount = xmlBeanDefinitionReader.loadBeanDefinitions(location);
        //输出加载的beanDefinition个数
        System.out.println(beanDefinitionsCount);

        lookupCollectionByType(beanFactory);
    }
    private static void lookupCollectionByType(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> users = listBeanFactory.getBeansOfType(User.class);
            for (Map.Entry<String,User> entry: users.entrySet()) {
                System.out.println("查找到的对象---" + entry.getValue());
            }
        }
    }
}
