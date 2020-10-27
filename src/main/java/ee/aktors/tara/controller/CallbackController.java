package ee.aktors.tara.controller;

import ee.aktors.tara.domain.IdToken;
import ee.aktors.tara.exception.AuthenticationException;
import ee.aktors.tara.service.CallbackService;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallbackController {

  private static final Logger logger = Logger.getLogger(CallbackController.class);

  private final CallbackService callbackService;

  public CallbackController(CallbackService callbackService) {
    this.callbackService = callbackService;
  }

  @GetMapping("/login/oidc")
  public String callBackFromTara(
      @RequestParam(required = false, name = "code") String code,
      @RequestParam(name = "state") String state,
      @RequestParam(required = false, name = "error") String error,
      @RequestParam(required = false, name = "error_description") String errorDescription,
      HttpSession httpSession,
      Model model
  ) throws AuthenticationException {

    if (code == null) {
      logger.warn(String.format("Error( %s ) from TARA: %s", error, errorDescription));
      throw new AuthenticationException(errorDescription, HttpStatus.UNAUTHORIZED);
    }

//    if (!state.equals(httpSession.getAttribute("csrfToken"))) {
//      logger.warn("State from Tara and csrfToken from session does not match");
//      throw new AuthenticationException("CSRF Authentication Failure", HttpStatus.UNAUTHORIZED);
//    }

    httpSession.setAttribute("auth_code", code);
    IdToken idToken = callbackService.getIdToken(code);

    model.addAttribute("personalId", idToken.getSub());
    model.addAttribute("firstName", idToken.getProfileAttributes().getGivenName());
    model.addAttribute("lastName", idToken.getProfileAttributes().getFamilyName());
    model.addAttribute("dateOfBirth", idToken.getProfileAttributes().getDateOfBirth());
    model.addAttribute("authMethod", idToken.getAmr());

    return "authenticated";
  }

  @GetMapping("/session/cancel")
  public String cancelFromTara() {
    logger.info("Client returns from TARA without an authentication");
    return "redirect:/";
  }

}
