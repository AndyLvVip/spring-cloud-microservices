package aspire.demo.learningspringbootchat.chat;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.stream.Collectors;

/**
 * Created by andy.lv
 * on: 2018/12/28 9:44
 */
public abstract class AuthorizedWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session
                .getHandshakeInfo()
                .getPrincipal()
                .filter(this::isAuthorized)
                .then(handleInternal(session));
    }

    protected abstract Mono<Void> handleInternal(WebSocketSession session);

    public boolean isAuthorized(Principal principal) {
        Authentication authentication = (Authentication) principal;
        return authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains("ROLE_USER");
    }
}
