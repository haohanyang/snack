package snack.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import snack.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;

    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests(
            request -> request.requestMatchers("/chat/**", "/api/v1/**").authenticated()
                .anyRequest().permitAll());
        http.oauth2Login(
            oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.oidcUserService(this.oidcUserService())));
        http.logout(logout -> logout.logoutSuccessUrl("/"));
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

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            var oidcUser = delegate.loadUser(userRequest);
            var info = userService.mapUserInfo(oidcUser.getUserInfo());
            Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), info);
        };
    }

}