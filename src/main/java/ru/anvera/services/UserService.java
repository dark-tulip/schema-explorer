package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.repos.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Long getProjectIdByUsername(String username) {
    return userRepository.findByUsername(username).getProjectId();
  }
}
