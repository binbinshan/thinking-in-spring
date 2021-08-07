package spring.dependency.injection;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 方法注入
 */
public class AnnotationDependencyMethodInjectionDemo {

    private UserHolder userHolder;
    private User user;
    private UserHolder userHolder2;

    @Autowired
    public void init1(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @Autowired
    public void init11(User user) {
        this.user = user;
    }

    @Resource
    public void init2(UserHolder userHolder2) {
        this.userHolder2 = userHolder2;
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencyMethodInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/annotation-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        AnnotationDependencyMethodInjectionDemo demo = applicationContext.getBean(AnnotationDependencyMethodInjectionDemo.class);
        // @Autowired 字段关联
        UserHolder userHolder = demo.userHolder;
        System.out.println(userHolder);
        System.out.println(demo.userHolder2);
        System.out.println(userHolder == demo.userHolder2);

        System.out.println(demo.user);
        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        return new UserHolder(user);
    }
}
