/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spring.ioc.domain;


import com.spring.ioc.enums.City;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * 用户类
 * @author shanbin
 */
@Getter
@Setter
public class User {

    private Long id;

    private String name;

    private Resource localResource;

    private City city;

    private City[] birthCity;

    private List<City> lifeCity;

    private List<City> workCity;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", localResource=" + localResource +
                ", city=" + city +
                ", birthCity=" + Arrays.toString(birthCity) +
                ", lifeCity=" + lifeCity +
                ", workCity=" + workCity +
                '}';
    }

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static User createUser() {
        User user = new User();
        user.setId(100L);
        user.setName("static-method-user");
        return user;
    }


}
