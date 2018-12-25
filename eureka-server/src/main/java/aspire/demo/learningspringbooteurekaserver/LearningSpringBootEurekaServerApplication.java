package aspire.demo.learningspringbooteurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LearningSpringBootEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningSpringBootEurekaServerApplication.class, args);
    }

}

