package aspire.demo.learningspringbootchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.SaveSessionGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.WebSession;

/**
 * Created by andy.lv
 * on: 2018/12/27 12:57
 */
@Configuration
public class GatewayConfig {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayConfig.class);

    static class CustomSaveSessionGatewayFilterFactory extends SaveSessionGatewayFilterFactory {

        @Override
        public GatewayFilter apply(Object config) {
            return ((exchange, chain) -> exchange.getSession()
                    .map(webSession -> {
                        LOG.info("Session id: " + webSession.getId());
                        webSession.getAttributes().entrySet()
                                .forEach(entry -> LOG.info(entry.getKey() + "=>" + entry.getValue()));
                        return webSession;
                    }).map(WebSession::save)
                    .then(chain.filter(exchange)));
        }
    }

    @Bean
    @Primary
    SaveSessionGatewayFilterFactory customSaveSessionGatewayFilterFactory() {
        return new CustomSaveSessionGatewayFilterFactory();
    }

}
