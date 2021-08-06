package spring.dependency.injection;

import com.spring.ioc.domain.User;

public class UserHolder {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserHolder{" +
                "user=" + user +
                '}';
    }
}
