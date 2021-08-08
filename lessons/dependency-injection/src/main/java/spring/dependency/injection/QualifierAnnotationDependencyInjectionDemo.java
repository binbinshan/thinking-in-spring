package spring.dependency.injection;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import spring.dependency.annotation.UserGroup;

import java.util.Collection;
import java.util.List;

/**
 * {@link Qualifier} 注解依赖注入
 * @author shanbin
 */
public class QualifierAnnotationDependencyInjectionDemo {

    /**
     * user bean  ->  primary = true
     */
    @Autowired
    private User user;

    /**
     * 根据指定 Bean 名称或ID 注入
     */
    @Autowired
    @Qualifier("superUser")
    private User nameUser;

    /**
     * 整体应用上下文存在 6 个 User 类型的 Bean
     * user
     * superUser
     * user1 -> @Qualifier
     * user2 -> @Qualifier
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    private List<User> allUsers;

    /**
     * 2 个 Qualifier user Bean + 2 个 UserGroup user Bean
     * user1 -> @Qualifier
     * user2 -> @Qualifier
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    @Qualifier
    private Collection<User> qualifiedUsers;

    /**
     * 基于 Qualifier 扩展
     * UserGroup 的 2个Bean
     * user3 -> @UserGroup
     * user4 -> @UserGroup
     */
    @Autowired
    @UserGroup
    private Collection<User> groupedUsers;

    /**
     * 注入user bean
     * Primary = true
     */
    @Bean
    @Primary
    public User user() {
        return createUser(10L);
    }

    /**
     * 注入superUser bean
     */
    @Bean
    public User superUser() {
        return createUser(100L);
    }

    /**
     * 进行逻辑分组 ，注入 user1 bean
     * primary = true
     */
    @Bean
    @Qualifier
    public User user1() {
        return createUser(1L);
    }

    /**
     * 进行逻辑分组 ，注入 user2 bean
     */
    @Bean
    @Qualifier
    public User user2() {
        return createUser(2L);
    }

    /**
     * 基于注解 @Qualifier 扩展限定，进行逻辑分组 ，注入 user3 bean
     */
    @Bean
    @UserGroup
    public  User user3() {
        return createUser(3L);
    }

    /**
     * 基于注解 @Qualifier 扩展限定，进行逻辑分组 ，注入 user4 bean
     */
    @Bean
    @UserGroup
    public  User user4() {
        return createUser(4L);
    }

    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(QualifierAnnotationDependencyInjectionDemo.class);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        QualifierAnnotationDependencyInjectionDemo demo = applicationContext.getBean(QualifierAnnotationDependencyInjectionDemo.class);

        System.out.println("user ：--------->" + demo.user);
        System.out.println("nameUser ：--------->" + demo.nameUser);
        System.out.println("allUsers ：--------->" + demo.allUsers);
        System.out.println("qualifiedUsers ：--------->" + demo.qualifiedUsers);
        System.out.println("groupedUsers ： --------->" + demo.groupedUsers);

    }
}
