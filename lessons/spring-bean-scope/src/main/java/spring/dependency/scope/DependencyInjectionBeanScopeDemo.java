package spring.dependency.scope;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * @author shanbin
 */
public class DependencyInjectionBeanScopeDemo {


    @Bean
    public static User singletonUser() {
        return createUser(System.nanoTime());
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static User prototypeUser() {
        return createUser(System.nanoTime());
    }
    private static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Autowired
    @Qualifier("singletonUser")
    private User singletonUser1;

    @Autowired
    @Qualifier("singletonUser")
    private User singletonUser2;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser1;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser2;

    @Autowired
    private Map<String, User> users;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DependencyInjectionBeanScopeDemo.class);
        applicationContext.refresh();
        scopeBeanByInjection(applicationContext);
        applicationContext.close();
    }

    private static void scopeBeanByInjection(AnnotationConfigApplicationContext applicationContext) {
        DependencyInjectionBeanScopeDemo demo = applicationContext.getBean(DependencyInjectionBeanScopeDemo.class);
        System.out.println(demo.singletonUser1);
        System.out.println(demo.singletonUser2);
        System.out.println(demo.prototypeUser1);
        System.out.println(demo.prototypeUser2);

        demo.users.entrySet().stream().forEach(System.out::println);
    }
}
