package spring.dependency.scope;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author shanbin
 */
public class DependencyLookupBeanScopeDemo {


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

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(DependencyLookupBeanScopeDemo.class);
        applicationContext.refresh();

        scopeBeanByLookup(applicationContext);

        applicationContext.close();
    }

    private static void scopeBeanByLookup(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 3; i++) {
            User singletonUser = applicationContext.getBean("singletonUser", User.class);
            System.out.println("singletonUser===" + singletonUser);

            User prototypeUser = applicationContext.getBean("prototypeUser", User.class);
            System.out.println("prototypeUser===" + prototypeUser);
        }
    }
}
