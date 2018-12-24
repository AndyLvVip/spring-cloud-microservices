package aspire.demo.learningspringbootchat;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by andy.lv
 * on: 2018/12/24 13:47
 */
public abstract class UserParsingHandshakeHandler implements WebSocketHandler {

    private final Map<String, String> userMap;

    public UserParsingHandshakeHandler() {
        userMap = new HashMap<>();
    }

    @Override
    public List<String> getSubProtocols() {
        return null;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        this.userMap.put(session.getId(), Stream.of(session.getHandshakeInfo().getUri().getQuery().split("&"))
                .map(s -> s.split("="))
                .filter(strings -> strings[0].equals("user"))
                .findFirst()
                .map(strings -> strings[1])
                .orElse("")
        );
        return handleInternal(session);
    }

    protected abstract Mono<Void> handleInternal(WebSocketSession session);

    public String getUser(String sessionId) {
        return this.userMap.get(sessionId);
    }
}
