package com.spring.bean.definition.factory;

import com.spring.ioc.domain.User;

/**
 * @author shanbin
 */
public interface UserFactory {
    default User createUser() {
        return User.createUser();
    }
}
