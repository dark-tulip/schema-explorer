package ru.anvera.configs;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
public class CustomUserPrincipal extends User {

  private final Long projectId;

  public CustomUserPrincipal(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             Long projectId) {
    super(username, password, authorities);
    this.projectId = projectId;
  }
}
