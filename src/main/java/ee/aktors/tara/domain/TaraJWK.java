package ee.aktors.tara.domain;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ee.aktors.tara.domain.adapters.TaraJwkDeserializer;

@JsonAdapter(TaraJwkDeserializer.class)
public class TaraJWK {

  @SerializedName("kty")
  public String keyType;
  @SerializedName("kid")
  public String keyId;
  @SerializedName("n")
  public String modulus;
  @SerializedName("e")
  public String exponent;

  public String getKeyType() {
    return keyType;
  }

  public String getKeyId() {
    return keyId;
  }

  public String getModulus() {
    return modulus;
  }

  public String getExponent() {
    return exponent;
  }

}
