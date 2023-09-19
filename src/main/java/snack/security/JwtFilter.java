package snack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        var pattern = Pattern.compile("(?<=/api/v1/users/@me)(.*)");
        var matcher = pattern.matcher(uri);
        var token = SecurityContextHolder.getContext().getAuthentication();
        if (matcher.find() && token instanceof JwtAuthenticationToken jwtToken) {
            var userId = token.getName();
            var targetUrl = uri.replace("@me", userId);
            response.sendRedirect(targetUrl);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
