package ru.anvera.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ru.anvera.services.UserService;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SecurityContextUtils {

  private final UserService userService;

  public @Nullable CustomUserPrincipal getPrincipal() {
    Object authentication = SecurityContextHolder.getContext().getAuthentication();

    // if user authorized via jwt token
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      String username  = jwtAuthenticationToken.getTokenAttributes().get("preferred_username").toString();
      Long   projectId = userService.getProjectIdByUsername(username);

      return new CustomUserPrincipal(username, "", Collections.emptyList(), projectId);
    }
    throw new RuntimeException("cannot load user from security Context");
  }
}
