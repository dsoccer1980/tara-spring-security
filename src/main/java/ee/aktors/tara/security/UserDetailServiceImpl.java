package ee.aktors.tara.security;

import ee.aktors.tara.exception.AuthenticationException;
import ee.aktors.tara.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SecurityUser securityUser = userRepository.getUserByUsername(username)
        .orElseThrow(() -> new AuthenticationException("User not found", HttpStatus.FORBIDDEN));

    return securityUser;
  }
}
