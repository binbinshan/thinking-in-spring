package spring.dependency.lookup.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author shanbin
 */
@Component
@Getter
@Setter
public class Person {
    private Long id;
    private String name;

    @PostConstruct
    private void init(){
        this.id = 100L;
        this.name = "人";
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
