package aspire.demo.learningspringboot.comment;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomerProcessor {
    String INPUT = "input";
    String OUTPUT = "emptyOutput";

    @Input(CustomerProcessor.INPUT)
    SubscribableChannel input();

    @Output(CustomerProcessor.OUTPUT)
    MessageChannel output();
}
