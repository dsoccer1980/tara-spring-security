package ee.aktors.tara.controller;

import ee.aktors.tara.util.TaraConfiguration;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {

  private static final Logger logger = Logger.getLogger(AuthController.class);

  private final String authenticationUrl;

  public AuthController(TaraConfiguration taraConfiguration) throws UnsupportedEncodingException {
    this.authenticationUrl = String
        .format("%s?client_id=%s&response_type=code&scope=%s&redirect_uri=%s&state=",
            taraConfiguration.getTARAAuthorizationEndpoint(),
            taraConfiguration.getClientId(),
            URLEncoder.encode(taraConfiguration.getScope(), StandardCharsets.UTF_8.name()),
            URLEncoder.encode(taraConfiguration.getRedirectUri(), StandardCharsets.UTF_8.name()));
  }

  @GetMapping(value = "")
  public String index() {
    return "index";
  }

  @GetMapping(value = "/authenticate")
  public View authenticate(HttpSession session) {
    logger.info("Sending client data to TARA for authentication");
    String csrfToken = UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
    return new RedirectView(
        authenticationUrl.concat(csrfToken),
        false,
        true,
        false);
  }

}