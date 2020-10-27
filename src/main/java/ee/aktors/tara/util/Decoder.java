package ee.aktors.tara.util;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class Decoder {

  public String base64UrlDecode(String input) {
    byte[] decodedBytes = base64UrlDecodeToBytes(input);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public byte[] base64UrlDecodeToBytes(String input) {
    Base64 decoder = new Base64(-1, null, true);
    return decoder.decode(input);
  }

}
