package ee.aktors.tara.domain;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class ProfileAttributes {


  @SerializedName("family_name")
  private String familyName;

  @SerializedName("given_name")
  private String givenName;

  @SerializedName("date_of_birth")
  private LocalDate dateOfBirth;

  public String getFamilyName() {
    return familyName;
  }

  public String getGivenName() {
    return givenName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

}