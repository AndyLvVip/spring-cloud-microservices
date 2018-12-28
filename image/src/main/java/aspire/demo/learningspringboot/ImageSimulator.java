package aspire.demo.learningspringboot;

import aspire.demo.learningspringboot.comment.Comment;
import aspire.demo.learningspringboot.image.ImageController;
import aspire.demo.learningspringboot.image.ImageRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.support.BindingAwareModelMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andy.lv
 * on: 2018/12/19 13:43
 */
@Component
@Profile("simulator")
public class ImageSimulator {

    private final ImageController imageController;
    private final AtomicInteger counter;
    private final ImageRepository imageRepository;


    public ImageSimulator(ImageController imageController, ImageRepository imageRepository) {
        this.imageController = imageController;
        this.counter = new AtomicInteger(1);
        this.imageRepository = imageRepository;
    }

    @EventListener
    public void simulateUserClicking(ApplicationReadyEvent event) {
        Flux.interval(Duration.ofMillis(500))
                .flatMap(tick -> Mono.defer(() -> imageController.index(null, new BindingAwareModelMap())))
                .subscribe();

    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {

        Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> imageRepository.findAll())
                .map(image -> {
                    Comment comment = new Comment();
                    comment.setImageId(image.getId());
                    comment.setComment("Comment #" + counter.getAndIncrement());
                    return Mono.just(comment);
                }).flatMap(comment -> Mono.defer(() -> imageController.addComment(comment)))
                .subscribe()
        ;
    }
}
