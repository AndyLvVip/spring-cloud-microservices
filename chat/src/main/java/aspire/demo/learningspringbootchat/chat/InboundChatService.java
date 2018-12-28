package aspire.demo.learningspringbootchat.chat;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(ChatServiceStreams.class)
public class InboundChatService extends AuthorizedWebSocketHandler {

    private final ChatServiceStreams chatServiceStreams;

    public InboundChatService(ChatServiceStreams chatServiceStreams) {
        this.chatServiceStreams = chatServiceStreams;
    }

    public Mono<?> broadcast(String message, WebSocketSession user) {

        return user.getHandshakeInfo().getPrincipal()
                .map(p -> chatServiceStreams.clientToBroker()
                        .send(MessageBuilder.withPayload(message)
                                .setHeader(ChatServiceStreams.USER_HEADER, p.getName())
                                .build()

                        )
                );
    }

    @Override
    public Mono<Void> handleInternal(WebSocketSession session) {
        return session
                .receive()
                .log(session.getId() + "inbound-incoming-chat-message")
                .map(WebSocketMessage::getPayloadAsText)
                .log(session.getId() + "inbound-convert-to-text")
                .log(session.getId() + "inbound-mark-with-session-id")

                .flatMap(msg -> broadcast(msg, session))
                .log(session.getId() + "inbound-broadcast-to broker")
                .then()
                ;
    }
}
