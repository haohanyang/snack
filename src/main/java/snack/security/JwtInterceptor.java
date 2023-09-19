package snack.security;

import java.util.regex.Pattern;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This interceptor is used to redirect the user to the correct endpoint based
 * on token information.
 * For example, if the user is trying to access /api/v1/users/@me, the
 * interceptor will redirect the user to
 * /api/v1/users/{user_id} based on the user id in the token.
 */

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        var uri = request.getRequestURI();
        var pattern = Pattern.compile("(?<=/api/v1/users/@me)(.*)");
        var matcher = pattern.matcher(uri);
        if (matcher.find() && request.getUserPrincipal() instanceof Jwt principal) {
            var userId = principal.getId();
            var targetUrl = uri.replace("@me", userId);
            request.getRequestDispatcher(targetUrl).forward(request, response);
            return false;
        }
        return true;
    }

}
