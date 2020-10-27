package ee.aktors.tara.repository;

import ee.aktors.tara.security.SecurityUser;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private static String NO_PASSWORD = "";

  @Override
  public Optional<SecurityUser> getUserByUsername(String username) {
    if ("EE60001019906".equals(username)) {
      return Optional.of(new SecurityUser(username, NO_PASSWORD, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), true));
    } else {
      return Optional.of(new SecurityUser(username, NO_PASSWORD, Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")), true));
    }
  }
}
