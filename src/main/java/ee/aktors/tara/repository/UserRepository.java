package ee.aktors.tara.repository;

import ee.aktors.tara.security.SecurityUser;
import java.util.Optional;

public interface UserRepository {

  Optional<SecurityUser> getUserByUsername(String username);

}
