package aspire.demo.learningspringbootchat.comment;

import aspire.demo.learningspringbootchat.chat.ChatServiceStreams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@EnableBinding(ChatServiceStreams.class)
public class CommentService implements WebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private ObjectMapper mapper;

    private Flux<Comment> flux;

    private FluxSink<Comment> webSocketCommentSink;

    public CommentService(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
        this.flux = Flux.<Comment>create(emitter -> this.webSocketCommentSink = emitter, FluxSink.OverflowStrategy.IGNORE)
                .publish().autoConnect()

        ;

    }

    @Override
    public List<String> getSubProtocols() {
        return null;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(this.flux.map(c -> {
                    try {
                        return this.mapper.writeValueAsString(c);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).log("encode-as-json")
                        .map(session::textMessage)
                        .log("wrap as websocket message")
                        .log("publish to websocket")

        );
    }

    @StreamListener(ChatServiceStreams.NEW_COMMENT)
    public void broadcast(Comment comment) {
        if (null != webSocketCommentSink) {
            LOG.info("Publishing " + comment + " to websocket...");
            webSocketCommentSink.next(comment);
        }
    }
}
