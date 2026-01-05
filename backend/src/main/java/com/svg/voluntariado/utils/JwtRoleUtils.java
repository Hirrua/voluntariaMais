package com.svg.voluntariado.utils;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;

public final class JwtRoleUtils {

    private JwtRoleUtils() {
    }

    public static boolean hasRole(Jwt principal, String role) {
        if (principal == null || role == null || role.isBlank()) {
            return false;
        }

        Object scopeClaim = principal.getClaim("scope");
        if (scopeClaim instanceof String scope) {
            return Arrays.asList(scope.split(" ")).contains(role);
        }

        if (scopeClaim instanceof Collection<?> values) {
            return values.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .anyMatch(role::equals);
        }

        return false;
    }
}
