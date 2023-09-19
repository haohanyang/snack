package snack.web.rest;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

@Slf4j
public class WebsocketController {
    @SubscribeMapping("/gateway/{user_id}")
    public void onSubscribe(@DestinationVariable("user_id") String userId, Principal principal) {
        log.info("User {} subscribed to gateway {}", principal.getName(), userId);
    }
}
