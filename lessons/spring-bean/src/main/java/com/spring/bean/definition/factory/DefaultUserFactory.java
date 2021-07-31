package com.spring.bean.definition.factory;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author shanbin
 */
public class DefaultUserFactory implements UserFactory, InitializingBean, DisposableBean {
    @Override
    public User createUser() {
        User user = new User();
        user.setId(100L);
        user.setName("bean-factory-user");
        return user;
    }
    @PostConstruct
    public void init(){
        System.out.println("@PostConstruct init : UserFactory 初始化");
    }

    public void initUserFactory(){
        System.out.println("initUserFactory init : UserFactory 初始化");
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet init : UserFactory 初始化");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("@PreDestroy destroy : UserFactory 销毁");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy : UserFactory 销毁");
    }

    public void destroyUserFactory() throws Exception {
        System.out.println("destroyUserFactory destroy : UserFactory 销毁");
    }


}
