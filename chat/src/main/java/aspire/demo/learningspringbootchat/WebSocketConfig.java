package aspire.demo.learningspringbootchat;

import aspire.demo.learningspringbootchat.chat.InboundChatService;
import aspire.demo.learningspringbootchat.chat.OutboundChatService;
import aspire.demo.learningspringbootchat.comment.CommentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketMapping(CommentService commentService,
                                           InboundChatService inboundChatService,
                                           OutboundChatService outboundChatService) {
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/topic/comment.new", commentService);
        urlMap.put("/topic/chatMessage.new", outboundChatService);
        urlMap.put("/app/chatMessage.new", inboundChatService);

        Map<String, CorsConfiguration> corsConfigurationMap = new HashMap<>();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:9000");
        corsConfigurationMap.put("/topic/comment.new", corsConfiguration);
        corsConfigurationMap.put("/topic/chatMessage.new", corsConfiguration);
        corsConfigurationMap.put("/app/chatMessage.new", corsConfiguration);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10);
        mapping.setUrlMap(urlMap);
        mapping.setCorsConfigurations(corsConfigurationMap);

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
