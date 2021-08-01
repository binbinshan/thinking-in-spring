package spring.dependency.lookup;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.dependency.lookup.domain.Person;

/**
 * 单一类型的依赖查找 基于 @{@link org.springframework.beans.factory.BeanFactory} 实现
 *
 * @author shanbin
 */

public class SingleTypeBeanFactoryDemo {

    public static void main(String[] args) {
        //通过注解上下文构建IOC容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        //注册 Person bean
        applicationContext.register(Person.class);
        //调用register() 需要手动refresh()
        applicationContext.refresh();

        //1. 根据 Bean 名称查找
        Person lookupPersonByName = (Person) applicationContext.getBean("person");
        System.out.println(lookupPersonByName);
        //2. 根据 Bean 类型查找
        Person lookupPersonByType = applicationContext.getBean(Person.class);
        System.out.println(lookupPersonByType);
        //3. 根据 Bean 名称 + 类型查找
        Person lookupPersonByNameAndType = applicationContext.getBean("person",Person.class);
        System.out.println(lookupPersonByNameAndType);
    }
}
