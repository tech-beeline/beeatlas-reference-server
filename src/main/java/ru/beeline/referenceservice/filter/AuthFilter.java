/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.filter;


import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.beeline.referenceservice.context.RequestContext;
import ru.beeline.referenceservice.domain.Product;
import ru.beeline.referenceservice.domain.User;
import ru.beeline.referenceservice.repository.ProductRepository;
import ru.beeline.referenceservice.repository.UserRepository;
import ru.beeline.referenceservice.util.AuthUtil;
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
    private final ProductRepository productRepository;

    public AuthFilter(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Исключаем Swagger UI и API документацию из фильтра аутентификации
        return path != null && (
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/v2/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/") ||
                path.startsWith("/actuator")
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        Optional<User> userOpt;
        String authHeader = cachedRequest.getHeader("Authorization");
        String xAuthHeader = cachedRequest.getHeader("X-Authorization");
        if (!validateHeaders(authHeader, xAuthHeader, response)) {
            return;
        }
        if (authHeader != null && !authHeader.isEmpty()) {
            userOpt = authenticateBasic(authHeader);
        } else {
            userOpt = authenticateXAuthorization(cachedRequest, response, xAuthHeader);
            if(userOpt.isEmpty()){
                return;
            }
        }
        if (userOpt.isEmpty() || !isAuthorized(cachedRequest, userOpt.get())) {
            if (userOpt.isEmpty()) {
                sendUnauthorized(response);
            } else {
                sendForbidden(response);
            }
            return;
        }
        User user = userOpt.get();
        RequestContext.setCurrentUser(user);
        try {
            filterChain.doFilter(cachedRequest, response);
        } finally {
            RequestContext.clear();
        }
    }

    private boolean validateHeaders(String authHeader, String xAuthHeader, HttpServletResponse response) throws IOException {
        if ((authHeader == null || authHeader.isEmpty()) && (xAuthHeader == null || xAuthHeader.isEmpty())) {
            sendUnauthorized(response);
            return false;
        }
        if (authHeader != null && !authHeader.isEmpty() && xAuthHeader != null && !xAuthHeader.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Only one authorization header allowed");
            return false;
        }
        return true;
    }

    private Optional<User> authenticateBasic(String authHeader) {
        if (!authHeader.startsWith("Basic ")) {
            return Optional.empty();
        }
        String base64Credentials = authHeader.substring("Basic ".length());
        try {
            String decoded = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            if (parts.length != 2) return Optional.empty();
            String login = parts[0];
            String password = parts[1];
            String passwordHash = PasswordUtil.sha256(password);
            return userRepository.findByLoginAndPassword(login, passwordHash);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<User> authenticateXAuthorization(HttpServletRequest request, HttpServletResponse response, String xAuthHeader) throws IOException {
        String nonce = request.getHeader("Nonce");
        if (nonce == null || nonce.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing Nonce header");
            return Optional.empty();
        }
        String[] parts = xAuthHeader.split(":", 2);
        if (parts.length != 2) {
            sendUnauthorized(response);
            return Optional.empty();
        }
        String apiKey = parts[0];
        String base64Signature = parts[1];
        Optional<Product> productOpt = productRepository.findByStructurizrApiKey(apiKey);
        if (productOpt.isEmpty()) {
            sendUnauthorized(response);
            return Optional.empty();
        }
        Product product = productOpt.get();
        String stringToSign = buildStringToSign(request, nonce);
        logger.info(stringToSign);
        String calculatedSignature = AuthUtil.hmacSha256(stringToSign, product.getStructurizrApiSecret());
        if (!calculatedSignature.equals(base64Signature)) {
            sendUnauthorized(response);
            return Optional.empty();
        }
        User user = new User();
        user.setLogin(apiKey);
        user.setAdmin(true);
        return Optional.of(user);
    }

    private String buildStringToSign(HttpServletRequest request, String nonce) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        byte[] bodyBytes = ((CachedBodyHttpServletRequest) request).getCachedBody();
        String md5Body = AuthUtil.md5Hex(bodyBytes);
        String contentType = Optional.ofNullable(request.getContentType()).orElse("");
        logger.info(method + "\n" + path + "\n" + md5Body + "\n" + contentType + "\n" + nonce + "\n");
        return method + "\n" + path + "\n" + md5Body + "\n" + contentType + "\n" + nonce + "\n";
    }

    private boolean isAuthorized(HttpServletRequest request, User user) {
        return "GET".equalsIgnoreCase(request.getMethod()) || isPasswordChange(request) || user.getAdmin();
    }

    private boolean isPasswordChange(HttpServletRequest request) {
        return "PATCH".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().matches("^/user/api/v1/users/\\d+/password$");
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        sendError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
