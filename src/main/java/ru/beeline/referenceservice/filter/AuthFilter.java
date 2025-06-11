package ru.beeline.referenceservice.filter;


import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.beeline.referenceservice.context.RequestContext;
import ru.beeline.referenceservice.domain.UserEntity;
import ru.beeline.referenceservice.repository.UserRepository;
import ru.beeline.referenceservice.util.PasswordUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public AuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendUnauthorized(response);
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String decoded;
        try {
            byte[] credBytes = Base64.getDecoder().decode(base64Credentials);
            decoded = new String(credBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            sendUnauthorized(response);
            return;
        }

        String[] parts = decoded.split(":", 2);
        if (parts.length != 2) {
            sendUnauthorized(response);
            return;
        }

        String login = parts[0];
        String password = parts[1];
        String passwordHash = PasswordUtil.sha256(password);

        Optional<UserEntity> userOpt = userRepository.findByLoginAndPassword(login, passwordHash);
        if (userOpt.isEmpty()) {
            sendUnauthorized(response);
            return;
        }

        UserEntity user = userOpt.get();

        boolean isGet = HttpMethod.GET.matches(request.getMethod());
        boolean isPasswordChangeEndpoint = isPasswordChange(request); // реализация позже
        if (!isGet && !isPasswordChangeEndpoint && !user.getAdmin()) {
            sendForbidden(response);
            return;
        }

        RequestContext.setCurrentUser(user);

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }

    private boolean isPasswordChange(HttpServletRequest request) {
        if (!request.getMethod().equals("POST")) {
            return false;
        }
        String uri = request.getRequestURI();
        return uri.matches("^/api/v1/users/\\d+/password$");
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Forbidden\"}");
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Not authorized\"}");
    }
}

