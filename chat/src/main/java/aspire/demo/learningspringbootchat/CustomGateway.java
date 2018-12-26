package aspire.demo.learningspringbootchat;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by andy.lv
 * on: 2018/12/26 10:27
 */
@Configuration
public class CustomGateway {


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        final String LB_IMAGE = "lb://IMAGE";
        return builder.routes()
                .route("imageService", r -> r.path("/imageService/**")
                        .filters(f -> f.rewritePath("/imageService/(?<segment>.*)", "/${segment}")
                                .rewritePath("/imageService", "/"))
                        .uri(LB_IMAGE)
                )
                .route("images", r -> r.path("/images/**")
                        .uri(LB_IMAGE)
                )
                .route("mainCss", r -> r.path("/main.css")
                        .uri(LB_IMAGE)
                )
                .route("imageComments", r -> r.path("/image/comments/**")
                        .uri(LB_IMAGE)
                )
                .build();
    }

}
