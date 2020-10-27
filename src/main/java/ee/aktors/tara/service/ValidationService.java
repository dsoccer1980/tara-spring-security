package ee.aktors.tara.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import ee.aktors.tara.domain.IdToken;
import ee.aktors.tara.domain.TaraJWK;
import ee.aktors.tara.exception.AuthenticationException;
import ee.aktors.tara.util.Decoder;
import ee.aktors.tara.util.TaraConfiguration;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  private static final Logger logger = Logger.getLogger(ValidationService.class);
  private static final Gson gson = new Gson();

  private final TaraConfiguration taraConfiguration;
  private final Decoder decoder;

  public ValidationService(TaraConfiguration taraConfiguration, Decoder decoder) {
    this.taraConfiguration = taraConfiguration;
    this.decoder = decoder;
  }

  public boolean isValidToken(IdToken idToken) throws AuthenticationException {

    if (!idToken.getIss().equalsIgnoreCase(taraConfiguration.getIssuer())) {
      logger.warn("isValidToken: The issuer of the certificate does not match");
      return false;
    }

    if (!idToken.getAud().equalsIgnoreCase(taraConfiguration.getClientId())) {
      logger.warn("isValidToken: The ClientId doesn't match");
      return false;
    }

    if ((idToken.getExp() - Instant.now().getEpochSecond()) <= 0) {
      logger.warn("isValidToken: Token has been expired");
      return false;
    }
    HttpResponse taraResponseForJWKSRequest = getTaraResponseForJWKSRequest();
    TaraJWK jwkFromTara = mapTaraJWKSResponseToPojo(taraResponseForJWKSRequest);
    if (!idToken.getKeyId().equalsIgnoreCase(jwkFromTara.getKeyId())) {
      logger.warn("isValidToken: KeyId doesn't match");
      return false;
    } else {
      PublicKey publicKey = getPublicKey(
          jwkFromTara.getModulus(),
          jwkFromTara.getExponent(),
          jwkFromTara.getKeyType());
      return verifyUsingPublicKey(idToken.getJwtData(), idToken.getSignature(), publicKey);
    }
  }

  private HttpResponse getTaraResponseForJWKSRequest() throws AuthenticationException {
    HttpGet getTaraKey = new HttpGet(taraConfiguration.getTARAKeyEndpoint());
    getTaraKey.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    HttpResponse response;
    try {
      HttpClient client = HttpClientBuilder.create().build();
      response = client.execute(getTaraKey);
    } catch (IOException e) {
      logger.error("getKeysAsJsonFromTara. Exception while JWKS request:", e);
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
      logger.error("failed TARAKeyEndpoint URI");
      throw new AuthenticationException(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
    return response;
  }

  private TaraJWK mapTaraJWKSResponseToPojo(HttpResponse httpResponse)
      throws AuthenticationException {
    String respEntity;
    try {
      respEntity = EntityUtils.toString(httpResponse.getEntity());
    } catch (IOException e) {
      logger.error("mapTaraJWKSResponseToPojo. Exception while JWKS entity parsing:", e);
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    TaraJWK taraJWK;
    try {
      taraJWK = gson.fromJson(respEntity, TaraJWK.class);
    } catch (JsonParseException e) {
      logger.error(e.getMessage());
      throw new AuthenticationException(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
    return taraJWK;
  }

  private PublicKey getPublicKey(String modulus, String exponent, String keyType)
      throws AuthenticationException {
    BigInteger bigIntModulus = new BigInteger(1, decoder.base64UrlDecodeToBytes(modulus));
    BigInteger bigIntExponent = new BigInteger(1, decoder.base64UrlDecodeToBytes(exponent));
    RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(bigIntModulus, bigIntExponent);

    try {
      return KeyFactory.getInstance(keyType).generatePublic(rsaPublicKeySpec);
    } catch (Exception ex) {
      logger.error("getPublicKey:", ex);
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private boolean verifyUsingPublicKey(byte[] data, byte[] signature, PublicKey pubKey)
      throws AuthenticationException {
    try {
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initVerify(pubKey);
      sig.update(data);
      return sig.verify(signature);
    } catch (GeneralSecurityException e) {
      logger.warn("verifyUsingPublicKey. Exception while identity signature verification:", e);
      throw new AuthenticationException("Exception while identity signature verification",
          HttpStatus.UNAUTHORIZED);
    }
  }

}
