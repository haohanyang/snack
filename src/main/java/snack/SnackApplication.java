package snack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SnackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnackApplication.class, args);
    }

}
