package aspire.demo.learningspringbootchat.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Arrays;

@Configuration
public class InitUsers {

    @Bean
    CommandLineRunner initializeUsers(MongoOperations operations) {
        return args -> {
            operations.dropCollection(User.class);
            operations.insertAll(Arrays.asList(new User(null, "admin", "admin", new String[]{"ROLE_USER", "ROLE_ADMIN"}),
                    new User(null, "andy", "password", new String[]{"ROLE_USER"})
            ));

            operations.findAll(User.class)
                    .forEach(u -> System.out.println("Loaded " + u));
        };
    }
}
