package ee.aktors.tara.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TaraConfiguration {

  private final Environment env;

  public TaraConfiguration(Environment env) {
    this.env = env;
  }

  public String getTARAAuthorizationEndpoint() {
    return env.getProperty("TARAAuthorizationEndpoint");
  }

  public String getScope() {
    return env.getProperty("Scope");
  }

  public String getClientId() {
    return env.getProperty("ClientId");
  }

  public String getRedirectUri() {
    return env.getProperty("RedirectUri");
  }

  public String getClientSecret() {
    return env.getProperty("ClientSecret");
  }

  public String getTARATokenEndpoint() {
    return env.getProperty("TARATokenEndpoint");
  }

  public String getIssuer() {
    return env.getProperty("Issuer");
  }

  public String getTARAKeyEndpoint() {
    return env.getProperty("TARAKeyEndpoint");
  }

}
