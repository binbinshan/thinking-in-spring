package com.spring.bean.definition.factory;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * factoryBean
 * @author shanbin
 */
public class UserFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        User user = new User();
        user.setId(100L);
        user.setName("factory-bean-user");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
