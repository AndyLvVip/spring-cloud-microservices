package aspire.demo.learningspringboot;

import aspire.demo.learningspringboot.image.Image;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by andy.lv
 * on: 2018/11/30 16:45
 */
@Component
public class InitDatabase {

    @Bean
    public CommandLineRunner initImageCollection(MongoOperations operations) {
        return args -> {
            operations.dropCollection(Image.class);
            operations.insertAll(Arrays.asList(new Image("1", "learning-spring-boot-cover.jpg", "andy"),
                    new Image("2", "learning-spring-boot-2nd-edition-cover.jpg", "andy"),
                    new Image("3", "bazinga.png", "admin")
            ));
            operations.findAll(Image.class).forEach(c -> System.out.println(c.toString()));
        };
    }
}
