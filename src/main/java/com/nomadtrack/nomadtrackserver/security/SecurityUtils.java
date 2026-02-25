package com.nomadtrack.nomadtrackserver.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtils {

    public static Long getCurrentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Long id) {
            return id;
        }
        if (principal instanceof String str) {

            if ("anonymousUser".equals(str)) {
                throw new RuntimeException("Not authenticated");
            }

            return Long.parseLong(str);
        }

        assert principal != null;
        throw new RuntimeException(
                "Unexpected principal type: " + principal.getClass());
    }

    public static boolean isAdmin() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }
}
