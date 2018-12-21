package aspire.demo.learninghystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
public class LearningHystrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningHystrixApplication.class, args);
	}

}

