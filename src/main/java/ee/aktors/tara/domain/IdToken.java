package ee.aktors.tara.domain;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class IdToken {

  private String jti;
  private String iss;
  private String aud;
  private String sub;
  private String state;
  private String nonce;
  private String email;
  private String keyId;
  private Long exp;
  private Long iat;
  private Long nbf;
  private byte[] signature;
  private byte[] jwtData;
  private List<String> amr = new ArrayList<>();

  @SerializedName("profile_attributes")
  private ProfileAttributes profileAttributes;

  @SerializedName("at_hash")
  private String atHash;

  @SerializedName("email_verified")
  private String emailVerified;

  public String getJti() {
    return jti;
  }

  public String getIss() {
    return iss;
  }

  public String getAud() {
    return aud;
  }

  public String getSub() {
    return sub;
  }

  public String getState() {
    return state;
  }

  public String getNonce() {
    return nonce;
  }

  public String getEmail() {
    return email;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public Long getExp() {
    return exp;
  }

  public Long getIat() {
    return iat;
  }

  public Long getNbf() {
    return nbf;
  }

  public byte[] getSignature() {
    return signature;
  }

  public void setSignature(byte[] signature) {
    this.signature = signature;
  }

  public byte[] getJwtData() {
    return jwtData;
  }

  public void setJwtData(byte[] jwtData) {
    this.jwtData = jwtData;
  }

  public List<String> getAmr() {
    return amr;
  }

  public ProfileAttributes getProfileAttributes() {
    return profileAttributes;
  }

  public String getAtHash() {
    return atHash;
  }

  public String getEmailVerified() {
    return emailVerified;
  }
}
