package aspire.demo.learningspringboot.comment;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Controller
@EnableBinding(Source.class)
public class CommentController {
    private FluxSink<Message<Comment>> commentSink;

    private Flux<Message<Comment>> flux;

    private final MeterRegistry meterRegistry;

    private final CommentRepository repository;

    public CommentController(MeterRegistry meterRegistry,
                             CommentRepository repository) {
        this.flux = Flux.<Message<Comment>>create(
                emitter -> this.commentSink = emitter,
                FluxSink.OverflowStrategy.IGNORE
        ).publish()
                .autoConnect()
        ;
        this.meterRegistry = meterRegistry;
        this.repository = repository;
    }

    @PostMapping("/comments")
    public Mono<String> addComment(Mono<Comment> newComment) {
        if(commentSink != null) {
            return newComment
                    .flatMap(c -> {
                        commentSink.next(MessageBuilder.withPayload(c).build());
                        meterRegistry.counter("comment.produced", "imageId", c.getImageId())
                                .increment();
                        return Mono.just("redirect:http://localhost:9000");
                    });
        }else {
            return Mono.just("redirect:http://localhost:9000");
        }
    }

    @StreamEmitter
    public void emit(@Output(Source.OUTPUT)FluxSender sender) {
        sender.send(this.flux);
    }

    @GetMapping("/comments/{imageId}")
    @ResponseBody
    public Flux<Comment> comments(@PathVariable String imageId) {
        return repository.findByImageId(imageId);
    }
}
