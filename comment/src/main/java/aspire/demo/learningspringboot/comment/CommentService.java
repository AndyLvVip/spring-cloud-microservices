package aspire.demo.learningspringboot.comment;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(Processor.class)
public class CommentService {
    private final CommentRepository repository;
    private final MeterRegistry meterRegistry;
    static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository repository,
                          MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @StreamListener
    @Output(Processor.OUTPUT)
    public Flux<Comment> save(@Input(Processor.INPUT) Flux<Comment> comments) {
        return repository.
                saveAll(comments)
                .map(c -> {
                    LOG.info("Saving new comment: " + c);
                    meterRegistry
                            .counter("comment.consumed", "imageId", c.getImageId())
                            .increment();
                    return c;
                });
    }

}
