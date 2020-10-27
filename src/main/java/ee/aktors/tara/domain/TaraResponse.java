package ee.aktors.tara.domain;

import com.google.gson.annotations.SerializedName;

public class TaraResponse {

  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("token_type")
  private String tokenType;

  @SerializedName("id_token")
  private String jWT;

  @SerializedName("expires_in")
  private Long expiresIn;

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public String getJWT() {
    return jWT;
  }

  public Long getExpiresIn() {
    return expiresIn;
  }

}