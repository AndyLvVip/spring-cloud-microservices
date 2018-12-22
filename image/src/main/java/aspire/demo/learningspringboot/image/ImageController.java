package aspire.demo.learningspringboot.image;

import aspire.demo.learningspringboot.comment.Comment;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by andy.lv
 * on: 2018/12/6 13:56
 */
@Controller
@EnableBinding(Source.class)
public class ImageController {

    private static final String FILE_NAME = "{filename:.+}";

    private final ImageService imageService;

    private final CommentService commentService;

    private FluxSink<Message<Comment>> commentSink;

    private Flux<Message<Comment>> flux;

    private final MeterRegistry meterRegistry;


    public ImageController(MeterRegistry meterRegistry,
                           ImageService imageService,
                           CommentService commentService) {
        this.flux = Flux.<Message<Comment>>create(
                emitter -> this.commentSink = emitter,
                FluxSink.OverflowStrategy.IGNORE
        ).publish()
                .autoConnect()
        ;
        this.meterRegistry = meterRegistry;
        this.imageService = imageService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", imageService.findAllImages()
                .flatMap(image -> Mono.just(image)
                        .zipWith(
                                Flux.fromIterable(commentService.getComments(image.getId())).collectList()
                        )
                ).map(imageAndComment -> new HashMap() {{
                    put("id", imageAndComment.getT1().getId());
                    put("name", imageAndComment.getT1().getName());
                    put("comments", imageAndComment.getT2());
                }})
        );
        model.addAttribute("extra", "DevTools can also detect changes too");
        return Mono.just("index");
    }

    @PostMapping("/images")
    public Mono<String> createImage(@RequestPart(name = "file") Flux<FilePart> files) {
        return imageService.createImage(files)
                .then(Mono.just("redirect:/"))
                ;
    }


    @GetMapping(value = "/images/" + FILE_NAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename).map(resource -> {
            try {
                return ResponseEntity.ok().contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()));
            } catch (IOException e) {
                return ResponseEntity.badRequest()
                        .body("Couldn't find " + filename + " => " + e.getMessage());
            }
        });
    }

    @DeleteMapping("/images/" + FILE_NAME)
    public Mono<String> deleteImage(@PathVariable String filename) {
        return imageService.deleteImage(filename)
                .then(Mono.just("redirect:/"))
                ;
    }

    @PostMapping("/image/comments")
    public Mono<String> addComment(Mono<Comment> newComment) {
        if(commentSink != null) {
            return newComment
                    .flatMap(c -> {
                        commentSink.next(MessageBuilder.withPayload(c).build());
                        meterRegistry.counter("comment.produced", "imageId", c.getImageId())
                                .increment();
                        return Mono.just("redirect:/");
                    });
        }else {
            return Mono.just("redirect:/");
        }
    }

    @StreamEmitter
    public void emit(@Output(Source.OUTPUT) FluxSender sender) {
        sender.send(this.flux);
    }
}
