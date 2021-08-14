package com.spring.ioc.domain;

public class UserHolder {

    private User user;

    private String description;

    public UserHolder() {}
    public UserHolder(User user) {
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserHolder{" +
                "user=" + user +
                ", description='" + description + '\'' +
                '}';
    }
}
