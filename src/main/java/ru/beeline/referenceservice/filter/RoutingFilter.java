/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.beeline.referenceservice.config.RouteConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RoutingFilter extends OncePerRequestFilter {

    private final RouteConfig routeConfig;
    private final RestTemplate restTemplate;

    public RoutingFilter(RouteConfig routeConfig, RestTemplate restTemplate) {
        this.routeConfig = routeConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        String path = wrappedRequest.getRequestURI();
        if (path == null || path.isEmpty()) {
            filterChain.doFilter(wrappedRequest, response);
            return;
        }
        String[] pathParts = path.split("/");
        if (pathParts.length < 2) {
            filterChain.doFilter(wrappedRequest, response);
            return;
        }
        String firstPart = pathParts[1];
        Map<String, String> routes = routeConfig.getRoutes();
        if (routes.containsKey(firstPart)) {
            try {
                String host = routes.get(firstPart);
                String queryString = wrappedRequest.getQueryString();
                String targetUrl = buildTargetUrl(path, firstPart, host, queryString);
                HttpHeaders headers = createHeaders(wrappedRequest);
                ResponseEntity<String> responseEntity = processProxyRequest(targetUrl, wrappedRequest, headers);
                copyResponse(responseEntity, response);
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                writeErrorResponse(response, ex.getStatusCode(), ex.getResponseHeaders(), ex.getResponseBodyAsString());
            } catch (Exception e) {
                log.error("Error while proxying request", e);
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        "Error while proxying request: " + e.getMessage());
            }
            return;
        }
        filterChain.doFilter(wrappedRequest, response);
    }

    private ResponseEntity<String> processProxyRequest(String targetUrl, HttpServletRequest request, HttpHeaders headers)
            throws IOException {
        byte[] requestBody = request.getInputStream().readAllBytes();
        HttpEntity<byte[]> entity = new HttpEntity<>(requestBody, headers);
        log.info("Proxying {} request to URL: {}", request.getMethod(), targetUrl);
        log.info("Request headers: {}", headers);
        log.info("Request body length: {}", requestBody.length);
        return restTemplate.exchange(
                targetUrl,
                HttpMethod.valueOf(request.getMethod()),
                entity,
                String.class
        );
    }

    private HttpHeaders createHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> headers.set(headerName, request.getHeader(headerName)));
        headers.remove(HttpHeaders.HOST);
        return headers;
    }

    private String buildTargetUrl(String path, String firstPart, String host, String queryString) {
        int prefixEndIndex = path.indexOf(firstPart) + firstPart.length();
        String newPath = path.length() > prefixEndIndex ? path.substring(prefixEndIndex) : "/";
        String targetUrl = host + newPath;
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }
        return targetUrl;
    }

    private void copyResponse(ResponseEntity<String> responseEntity, HttpServletResponse response) throws IOException {
        response.setStatus(responseEntity.getStatusCodeValue());
        MediaType contentType = responseEntity.getHeaders().getContentType();
        if (contentType != null) {
            response.setContentType(contentType.toString());
            if (contentType.getCharset() != null) {
                response.setCharacterEncoding(contentType.getCharset().name());
            } else {
                response.setCharacterEncoding("UTF-8");
            }
        } else {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
        }
        responseEntity.getHeaders().forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Content-Length")
                    && !key.equalsIgnoreCase("Transfer-Encoding")
                    && !key.equalsIgnoreCase("Content-Type")) {
                values.forEach(value -> response.addHeader(key, value));
            }
        });
        if (responseEntity.hasBody()) {
            response.getWriter().write(Objects.requireNonNull(responseEntity.getBody()));
        }
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, HttpHeaders headers,
                                    String responseBody) throws IOException {
        response.setStatus(status.value());
        if (headers != null) {
            headers.forEach((key, values) -> {
                if (!key.equalsIgnoreCase("Content-Length")
                        && !key.equalsIgnoreCase("Transfer-Encoding")
                        && !key.equalsIgnoreCase("Content-Type")) {
                    values.forEach(value -> response.addHeader(key, value));
                }
            });
            MediaType contentType = headers.getContentType();
            if (contentType != null) {
                response.setContentType(contentType.toString());
                if (contentType.getCharset() != null) {
                    response.setCharacterEncoding(contentType.getCharset().name());
                } else {
                    response.setCharacterEncoding("UTF-8");
                }
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
            }
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        }
        if (responseBody != null) {
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        }
    }
}

