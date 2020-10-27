package ee.aktors.tara.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import ee.aktors.tara.domain.IdToken;
import ee.aktors.tara.domain.TaraResponse;
import ee.aktors.tara.exception.AuthenticationException;
import ee.aktors.tara.util.Decoder;
import ee.aktors.tara.util.TaraConfiguration;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class CallbackService {

  private static final Logger logger = Logger.getLogger(CallbackService.class);
  private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(LocalDate.class,
          (JsonDeserializer<LocalDate>) (json, typeOfT, jsonDeserializationContext) ->
              LocalDate.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .create();

  private final TaraConfiguration taraConfiguration;
  private final ValidationService validationService;
  private final Decoder decoder;

  public CallbackService(TaraConfiguration taraConfiguration,
      ValidationService validationService, Decoder decoder) {
    this.taraConfiguration = taraConfiguration;
    this.validationService = validationService;
    this.decoder = decoder;
  }

  public IdToken getIdToken(String code) throws AuthenticationException {
    TaraResponse taraResponse = requestUserDataFromTara(code);
    IdToken idToken = mapJWTToPojo(taraResponse.getJWT());
    if (validationService.isValidToken(idToken)) {
      return idToken;
    } else {
      throw new AuthenticationException("Token is not valid", HttpStatus.UNAUTHORIZED);
    }
  }

  private TaraResponse requestUserDataFromTara(String code) throws AuthenticationException {
    HttpPost tokenPostRequest = createTokenPostRequest(code);
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponse httpResponse;
    String respEntity;
    try {
      httpResponse = httpClient.execute(tokenPostRequest);
      respEntity = EntityUtils.toString(httpResponse.getEntity());
    } catch (IOException e) {
      logger.error("requestUserDataFromTara: exception while IdToken request execution. ", e);
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
      logger.error("requestUserDataFromTara: (400 Bad Request) response from TARA");
      throw new AuthenticationException(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
    return gson.fromJson(respEntity, TaraResponse.class);
  }

  private HttpPost createTokenPostRequest(String code) throws AuthenticationException {
    HttpPost httpPostTARAToken = new HttpPost(taraConfiguration.getTARATokenEndpoint());
    String clientIdAndSecret =
        String
            .format("%s:%s", taraConfiguration.getClientId(), taraConfiguration.getClientSecret());
    String authorizationString = "Basic "
        .concat(Base64.encodeBase64String(clientIdAndSecret.getBytes()));

    httpPostTARAToken
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    httpPostTARAToken.addHeader(HttpHeaders.AUTHORIZATION, authorizationString);
    httpPostTARAToken.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    List<NameValuePair> postBody = new ArrayList<>();
    postBody.add(new BasicNameValuePair("grant_type", "authorization_code"));
    postBody.add(new BasicNameValuePair("code", code));
    postBody.add(new BasicNameValuePair("redirect_uri", taraConfiguration.getRedirectUri()));

    try {
      httpPostTARAToken.setEntity(new UrlEncodedFormEntity(postBody));
      return httpPostTARAToken;
    } catch (UnsupportedEncodingException e) {
      logger.error("createTokenPostRequest: exception while body set. ", e);
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private IdToken mapJWTToPojo(String jwt) throws AuthenticationException {
    String[] listOfJwtParts = jwt.split("\\.");
    if (listOfJwtParts.length < 3) {
      logger.error("mapJWTToPojo: wrong count of JWT parts");
      throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    IdToken idToken = gson.fromJson(decoder.base64UrlDecode(listOfJwtParts[1]), IdToken.class);
    idToken.setSignature(decoder.base64UrlDecodeToBytes(listOfJwtParts[2]));
    byte[] jwtData = String.format("%s.%s", listOfJwtParts[0], listOfJwtParts[1])
        .getBytes(StandardCharsets.UTF_8);
    idToken.setJwtData(jwtData);
    String keyId = gson
        .fromJson(decoder.base64UrlDecode(listOfJwtParts[0]), JsonObject.class)
        .get("kid").getAsString();
    idToken.setKeyId(keyId);
    return idToken;
  }

}
