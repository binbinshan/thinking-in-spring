package com.spring.bean.definition;

import com.spring.ioc.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 *
 * {@link org.springframework.beans.factory.config.BeanDefinition} 构建BeanDefinition
 * @author shanbin
 */
public class BeanDefinitionCreationDemo {

    public static void main(String[] args) {
        // 1.通过 BeanDefinitionBuilder 构建
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        //bean 属性依赖注入
        beanDefinitionBuilder.addPropertyValue("id",2).addPropertyValue("name","binbinshan");
        //bean 作用域
        beanDefinitionBuilder.setScope("singleton");
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        // 2.通过 AbstractBeanDefinition 以及派生类,以GenericBeanDefinition为例
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        //add == addPropertyValue
        mutablePropertyValues.add("id",3).add("name","binbinshan");
        //设置bean class
        genericBeanDefinition.setBeanClass(User.class);
        genericBeanDefinition.setScope("singleton");
        genericBeanDefinition.setPropertyValues(mutablePropertyValues);

    }
}
