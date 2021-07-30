package com.spring.ioc.repository;

import com.spring.ioc.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author shanbin
 */
@Getter
@Setter
public class UserRepository {

    /**
     * 自定义单个Bean
     */
    private User user;
    /**
     * 自定义集合 Bean
     */
    private List<User> users;

    /**
     * 內建非 Bean 对象（依赖）
     */
    private BeanFactory beanFactory;
    private ObjectFactory<ApplicationContext> objectFactory;
}
