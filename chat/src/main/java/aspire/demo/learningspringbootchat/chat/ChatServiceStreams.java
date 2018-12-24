package aspire.demo.learningspringbootchat.chat;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ChatServiceStreams {
    String NEW_COMMENT = "newComment" ;

    String CLIENT_TO_BROKER = "clientToBroker" ;

    String BROKER_TO_CLIENT = "brokerToClient" ;

    String USER_HEADER = "user" ;

    @Input(NEW_COMMENT)
    SubscribableChannel newComment();

    @Output(CLIENT_TO_BROKER)
    MessageChannel clientToBroker();

    @Input(BROKER_TO_CLIENT)
    SubscribableChannel brokerToClient();
}
