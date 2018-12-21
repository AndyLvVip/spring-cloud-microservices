package aspire.demo.learningspringboot.comment;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends Repository<Comment, String> {

    Flux<Comment> findByImageId(String imageId);

    Mono<Comment> save(Comment newComment);

    Flux<Comment> saveAll(Flux<Comment> comments);

    Mono<Comment> findById(String id);

}
