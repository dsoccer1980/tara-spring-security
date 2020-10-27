package ee.aktors.tara.config;

import ee.aktors.tara.security.JwtTokenFilter;
import ee.aktors.tara.service.CallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final CallbackService callbackService;

  @Autowired
  public SecurityConfig(CallbackService callbackService) {
    this.callbackService = callbackService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .addFilterAfter(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/authenticate").permitAll()
        .antMatchers("/login/oidc").permitAll()
        .antMatchers("/user").hasRole("USER")
        .anyRequest().authenticated()
        .and()
        .formLogin().loginPage("/authenticate")
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
        .deleteCookies("JSESSIONID")
        .clearAuthentication(true)
        .invalidateHttpSession(true)
        .logoutSuccessUrl("/");
  }

  @Bean
  public JwtTokenFilter jwtTokenFilter() {
    return new JwtTokenFilter(callbackService);
  }
}
