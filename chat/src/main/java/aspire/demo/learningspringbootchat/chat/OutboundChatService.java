package aspire.demo.learningspringbootchat.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.security.Principal;
import java.util.Arrays;

@Service
@EnableBinding(ChatServiceStreams.class)
public class OutboundChatService extends AuthorizedWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OutboundChatService.class);

    private Flux<Message<String>> flux;

    private FluxSink<Message<String>> chatMessageSink;

    public OutboundChatService() {
        super();
        this.flux = Flux.<Message<String>>create(emitter -> this.chatMessageSink = emitter, FluxSink.OverflowStrategy.IGNORE)
                .publish()
                .autoConnect()
        ;
    }

    @StreamListener(ChatServiceStreams.BROKER_TO_CLIENT)
    public void listen(Message<String> message) {
        if (chatMessageSink != null) {
            LOG.info("Publishing " + message + " to websocket...");
            chatMessageSink.next(message);
        }
    }

    @Override
    public Mono<Void> handleInternal(WebSocketSession session) {
        return
                session.send(this.flux
                        .filter(msg -> validate(msg, session))
                        .map(this::transfer)
                        .map(session::textMessage)
                )
                        .log(session.getId() + "-outbound-wrap-as-websocket-message")
                        .log(session.getId() + "-outbound-publish-to-websocket")
                ;
    }

    private boolean validate(Message<String> message, WebSocketSession user) {
        if (message.getPayload().startsWith("@")) {
            String targetUser = message.getPayload().substring(1, message.getPayload().indexOf(" "));
            String sender = message.getHeaders().get(ChatServiceStreams.USER_HEADER, String.class);
            Principal principal = user.getHandshakeInfo().getPrincipal().block();
            return null != principal && Arrays.asList(targetUser, sender).contains(principal.getName());
        } else {
            return true;
        }
    }

    private String transfer(Message<String> message) {
        String user = message.getHeaders().get(ChatServiceStreams.USER_HEADER, String.class);
        if (message.getPayload().startsWith("@")) {
            return "(" + user + "): " + message.getPayload();
        } else {
            return "(" + user + ")(all): " + message.getPayload();
        }
    }
}
