package aspire.demo.learningspringbootchat.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Created by andy.lv
 * on: 2018/12/26 13:33
 */
@Controller
public class ChatController {

    @GetMapping("/")
    public Mono<String> index() {
        return Mono.just("index");
    }
}
