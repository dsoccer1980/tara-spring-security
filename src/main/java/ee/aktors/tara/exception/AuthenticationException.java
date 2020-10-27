package ee.aktors.tara.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException {

  private static final long serialVersionUID = 9126951652489627731L;

  private HttpStatus httpStatus;
  private LocalDateTime localDateTime;

  public AuthenticationException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
    this.localDateTime = LocalDateTime.now();
  }

  public AuthenticationException(HttpStatus httpStatus) {
    this(httpStatus.getReasonPhrase(), httpStatus);
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }

}
