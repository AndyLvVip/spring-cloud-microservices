package aspire.demo.learningspringboot;

import aspire.demo.learningspringboot.comment.Comment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * Created by andy.lv
 * on: 2018/11/30 16:45
 */
@Component
public class InitDatabase {

    @Bean
    public CommandLineRunner initCommentCollection(MongoOperations operations) {
        return args -> operations.dropCollection(Comment.class);
    }
}
