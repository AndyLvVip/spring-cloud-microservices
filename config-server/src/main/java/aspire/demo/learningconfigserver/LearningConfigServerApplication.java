package aspire.demo.learningconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class LearningConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningConfigServerApplication.class, args);
	}

}

