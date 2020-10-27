package ee.aktors.tara.domain.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import ee.aktors.tara.domain.TaraJWK;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.apache.http.util.TextUtils;
import org.springframework.util.ReflectionUtils;

public class TaraJwkDeserializer implements JsonDeserializer<TaraJWK> {

  @Override
  public TaraJWK deserialize(JsonElement jsonElement, Type typeOfT,
      JsonDeserializationContext context) {
    JsonObject jwksJson = jsonElement.getAsJsonObject();

    checkJsonForValidKeysArray(jwksJson);

    JsonObject jsonKey = jwksJson.get("keys").getAsJsonArray().get(0).getAsJsonObject();
    Field[] declaredFields = TaraJWK.class.getDeclaredFields();

    checkJsonForMissingAndBlankFields(jsonKey, declaredFields);
    checkJsonForExtraFields(jsonKey, declaredFields);

    TaraJWK taraJWK = new TaraJWK();
    for (Field declaredField : declaredFields) {
      String fieldName = getFieldNameOrSerializedNameIfPresent(declaredField);
      declaredField.setAccessible(true);
      ReflectionUtils.setField(declaredField, taraJWK, jsonKey.get(fieldName).getAsString());
    }

    return taraJWK;
  }

  private void checkJsonForValidKeysArray(JsonObject jwksJson) {
    if (!jwksJson.has("keys") && jwksJson.get("keys").getAsJsonArray().size() < 1) {
      throw new JsonParseException(
          "TaraJWK Deserializer: \"keys\" JsonArray is missing or has other name");
    }
  }

  private void checkJsonForMissingAndBlankFields(JsonObject jsonKey, Field[] declaredFields) {
    Arrays.stream(declaredFields)
        .map(this::getFieldNameOrSerializedNameIfPresent)
        .filter(fieldName -> isFieldPresentAndNotBlank(jsonKey, fieldName))
        .findAny()
        .ifPresent(field -> {
          throw new JsonParseException(
              String.format(
                  "TaraJWK Deserializer: field - \"%s\" is missing or blank at json from TARA",
                  field));
        });
  }

  private boolean isFieldPresentAndNotBlank(JsonObject jsonKey, String fieldName) {
    return !jsonKey.has(fieldName) || TextUtils
        .isBlank(jsonKey.get(fieldName).getAsString());
  }

  private void checkJsonForExtraFields(JsonObject jsonKey, Field[] declaredFields) {
    if (jsonKey.size() > declaredFields.length) {
      throw new JsonParseException(
          "TaraJWK Deserializer: JWK from tara has more fields than TaraJWK");
    }
  }

  private String getFieldNameOrSerializedNameIfPresent(Field field) {
    if (field.isAnnotationPresent(SerializedName.class)) {
      return field.getAnnotation(SerializedName.class).value();
    } else {
      return field.getName();
    }
  }

}
