package com.svg.voluntariado.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public JwtCookieFilter(JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean alreadyAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null;
        boolean headerPresent = StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (!alreadyAuthenticated && !headerPresent) {
            String token = extractCookieHeader(request.getCookies());

            if (StringUtils.hasText(token)) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    AbstractAuthenticationToken authenticationToken = jwtAuthenticationConverter.convert(jwt);
                    if (authenticationToken != null) {
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (JwtException err) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/api/ong/info") ||
               path.startsWith("/api/ong/info/") ||
               path.startsWith("/info/about/") ||
               (path.equals("/api/inscricao/confirmar") && "GET".equals(request.getMethod()));
    }

    public String extractCookieHeader(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if ("access_volunteer".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
