package snack.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import java.security.Principal;

public class WebsocketController {
    private final Logger logger = LoggerFactory.getLogger(WebsocketController.class);

    @SubscribeMapping("/gateway/{user_id}")
    public void onSubscribe(@DestinationVariable("user_id") String userId, Principal principal) {
        logger.info("User {} subscribed to gateway {}", principal.getName(), userId);
    }
}
