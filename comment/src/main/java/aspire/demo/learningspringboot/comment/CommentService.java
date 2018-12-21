package aspire.demo.learningspringboot.comment;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(CustomerProcessor.class)
public class CommentService {
    private final CommentRepository repository;
    private final MeterRegistry meterRegistry;

    public CommentService(CommentRepository repository,
                          MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @StreamListener
    @Output(CustomerProcessor.OUTPUT)
    public Flux<Void> save(@Input(CustomerProcessor.INPUT) Flux<Comment> comments) {
        return repository.
                saveAll(comments)
                .flatMap(c -> {
                    meterRegistry
                            .counter("comment.consumed", "imageId", c.getImageId())
                            .increment();
                    return Mono.empty();
                });
    }

}
