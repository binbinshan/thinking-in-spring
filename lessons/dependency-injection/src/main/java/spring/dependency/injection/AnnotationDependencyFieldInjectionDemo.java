package spring.dependency.injection;

import com.spring.ioc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 字段注入
 */
public class AnnotationDependencyFieldInjectionDemo {

    @Autowired
    private UserHolder userHolder;
    @Resource
    private UserHolder userHolder2;

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class（配置类）
        applicationContext.register(AnnotationDependencyFieldInjectionDemo.class);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        String xmlResourcePath = "classpath:/META-INF/annotation-dependency-constructor-injection-context.xml";
        // 加载 XML 资源，解析并且生成 BeanDefinition
        beanDefinitionReader.loadBeanDefinitions(xmlResourcePath);
        // 启动 Spring 应用上下文
        applicationContext.refresh();
        // 依赖查找并且创建 Bean
        AnnotationDependencyFieldInjectionDemo demo = applicationContext.getBean(AnnotationDependencyFieldInjectionDemo.class);
        // @Autowired 字段关联
        UserHolder userHolder = demo.userHolder;
        System.out.println(userHolder);
        System.out.println(demo.userHolder2);
        System.out.println(userHolder == demo.userHolder2);

        // 显示地关闭 Spring 应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user){
        return new UserHolder(user);
    }
}
