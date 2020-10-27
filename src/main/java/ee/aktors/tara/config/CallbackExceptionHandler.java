package ee.aktors.tara.config;

import ee.aktors.tara.exception.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CallbackExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ModelAndView handleAuthenticationException(AuthenticationException e) {
    ModelAndView error = new ModelAndView("error");
    error.getModelMap().addAttribute("description", e.getMessage());
    error.getModelMap().addAttribute("time", e.getLocalDateTime());
    error.getModelMap().addAttribute("status", e.getHttpStatus().value());
    error.setStatus(e.getHttpStatus());
    return error;
  }

}
