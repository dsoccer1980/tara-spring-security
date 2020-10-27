package ee.aktors.tara.controller;

import ee.aktors.tara.domain.IdToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping(value = "")
  public String index() {
    return "index";
  }

  @GetMapping("/authenticated")
  public String authenticatedWelcome(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    IdToken idToken = ((IdToken) authentication.getPrincipal());

    model.addAttribute("personalId", idToken.getSub());
    model.addAttribute("firstName", idToken.getProfileAttributes().getGivenName());
    model.addAttribute("lastName", idToken.getProfileAttributes().getFamilyName());
    model.addAttribute("dateOfBirth", idToken.getProfileAttributes().getDateOfBirth());
    model.addAttribute("authMethod", idToken.getAmr());
    return "authenticated";
  }

  @GetMapping("/user")
  public String user(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    IdToken idToken = ((IdToken) authentication.getPrincipal());

    model.addAttribute("personalId", idToken.getSub());
    return "user";
  }


}
