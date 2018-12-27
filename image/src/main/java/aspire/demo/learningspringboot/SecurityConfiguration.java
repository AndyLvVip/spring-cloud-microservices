package aspire.demo.learningspringboot;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain springWebFilterChain() {
        return ServerHttpSecurity.http()
                .csrf().disable()
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .build();
    }
}
