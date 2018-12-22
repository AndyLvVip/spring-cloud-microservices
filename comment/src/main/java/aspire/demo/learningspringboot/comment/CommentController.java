package aspire.demo.learningspringboot.comment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
public class CommentController {

    private final CommentRepository repository;

    public CommentController(CommentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/comments/{imageId}")
    @ResponseBody
    public Flux<Comment> comments(@PathVariable String imageId) {
        return repository.findByImageId(imageId);
    }
}
