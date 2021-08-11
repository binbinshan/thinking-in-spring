package spring.dependency.scope;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Map;

public class PrototypeBeanDestroyDemo implements DisposableBean {

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
    @Qualifier("prototypeUser")
    private User prototypeUser1;

    @Autowired
    @Qualifier("prototypeUser")
    private User prototypeUser2;

    @Autowired
    private Map<String, User> users;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(PrototypeBeanDestroyDemo.class);
        applicationContext.refresh();
        applicationContext.close();
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("当前 BeanScopeDemo Bean 正在销毁中...");
        this.prototypeUser1.destroy();
        this.prototypeUser2.destroy();
        // 销毁集合中的 Prototype Bean
        for (Map.Entry<String,User> entry:this.users.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            // 判断 beanDefinition 是否是原型模式，因为 Singleton Bean 会自己进行销毁
            if(beanDefinition.isPrototype()){
                User user = entry.getValue();
                user.destroy();
            }
        }
    }

}
