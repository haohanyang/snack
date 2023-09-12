package snack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import snack.security.JwtFilter;

import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final Environment env;

    public WebSecurityConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(
                csrf -> csrf.disable());

        var activeProfile = env.getProperty("spring.profiles.active");

        if (Objects.equals(activeProfile, "dev")) {
            // Allow cors only in dev mode
            http.cors(
                    cors -> cors.configurationSource(
                            request -> {
                                var corsConfiguration = new CorsConfiguration();
                                corsConfiguration.setAllowedOrigins(List.of("*"));
                                corsConfiguration.setAllowedMethods(List.of("*"));
                                corsConfiguration.setAllowedHeaders(List.of("*"));
                                return corsConfiguration;
                            }));
        }

        http.sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(
                request -> request.requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll());
        http.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        http.addFilterAfter(new JwtFilter(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public MessageMatcherDelegatingAuthorizationManager.Builder builder() {
        return new MessageMatcherDelegatingAuthorizationManager.Builder();
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages.anyMessage().authenticated();
        return messages.build();
    }
}