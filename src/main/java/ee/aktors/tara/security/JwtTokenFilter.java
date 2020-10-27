package ee.aktors.tara.security;

import ee.aktors.tara.domain.IdToken;
import ee.aktors.tara.exception.AuthenticationException;
import ee.aktors.tara.service.CallbackService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class JwtTokenFilter extends GenericFilterBean {

  private final CallbackService callbackService;
  private final UserDetailsService userDetailsService;

  public JwtTokenFilter(CallbackService callbackService, UserDetailsService userDetailsService) {
    this.callbackService = callbackService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    String code = servletRequest.getParameter("code");
    if (code != null) {
      try {
        IdToken idToken = callbackService.getIdToken(code);
        UserDetails userDetails = userDetailsService.loadUserByUsername(idToken.getSub());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (AuthenticationException e) {
        SecurityContextHolder.clearContext();
        ((HttpServletResponse) servletResponse).sendError(e.getHttpStatus().value());
        throw new AuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
