package aspire.demo.learningspringbootchat;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringCloudApplication
@EnableEurekaClient
public class LearningSpringBootChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningSpringBootChatApplication.class, args);
    }

}

