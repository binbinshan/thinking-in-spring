package com.spring.ioc.beandefiniton;

import com.spring.ioc.domain.SuperUser;
import com.spring.ioc.domain.User;
import com.spring.ioc.domain.UserHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.ObjectUtils;

public class BeanInstantiationLifecycleDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcess());

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:/META-INF/dependency-lookup-context.xml";
        int count = reader.loadBeanDefinitions(location);
        System.out.println("Bean 定义的数量: "+count);

        User user = beanFactory.getBean("user", User.class);
        SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);


        System.out.println(user);
        System.out.println(superUser);
        System.out.println(userHolder);
    }


    static class MyInstantiationAwareBeanPostProcess implements InstantiationAwareBeanPostProcessor {
        /**
         * 实例化前阶段
         */
        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals(beanName, "superUser") && beanClass.equals(SuperUser.class)) {
                SuperUser user = new SuperUser();
                user.setName("binbinshan-b");
                user.setAddress("BEIJING");
                return user;
            }
            return null;
        }


        /**
         * 实例化后阶段
         */
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals(beanName, "user") && User.class.equals(bean.getClass())) {
                //"user" 对象不允许属性赋值（配置元信息-> 属性值）
                User user = User.class.cast(bean);
                user.setId(2L);
                user.setName("after-superUser v2");
                return false;//返回 false 表示忽略掉配置元信息，比如 <bean ...<property name = id value = 1/>
            }
            return true;
        }

        //这里的user,是不会执行该回调的，这是因为在上面postProcessAfterInstantiation中拦截，返回false,
        //所以将阻止在user实例上调用任何后续的InstantiationAwareBeanPostProcessor实例。(postProcessProperties就是)

        //而superUser更是在postProcessBeforeInstantiation方法进行了操作，返回的是一个代理bean,更是完全跳过实例化阶段。

        //所以这两个bean 无法执行属性赋值前操作，需要一个新的userHolder来进行回调。
        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals(beanName, "userHolder") && UserHolder.class.equals(bean.getClass())) {
                final MutablePropertyValues propertyValues;
                // 兼容 pvs 为空的情况 pvs 对应xml中配置的 <property .../>
                if (pvs instanceof MutablePropertyValues) {
                    propertyValues = (MutablePropertyValues) pvs;
                } else {
                    propertyValues = new MutablePropertyValues();
                }
                if(propertyValues.contains("description")){
                    // PropertyValue 无法直接覆盖，因为是 final 类型
                    PropertyValue name = propertyValues.getPropertyValue("description");
                    propertyValues.removePropertyValue("description");
                    propertyValues.addPropertyValue("description","binbinshan v2");
                }
                return propertyValues;
            }
            return null;
        }
    }
}
