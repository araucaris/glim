package dev.varion.glim.gui;

import static java.util.Optional.ofNullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public final class GuiUtils {

  private static final Gson GSON = new Gson();

  private GuiUtils() {}

  public static String getSkinUrl(final String texture) {
    final String rawTexture =
        new String(Base64.getDecoder().decode(texture), StandardCharsets.UTF_8);
    final JsonObject textureJson = GSON.fromJson(rawTexture, JsonObject.class);
    final JsonElement textureElement = textureJson.get("textures");
    if (Objects.isNull(textureElement)) {
      return null;
    }

    final JsonElement skinElement = textureElement.getAsJsonObject().get("SKIN");
    if (Objects.isNull(skinElement)) {
      return null;
    }

    return ofNullable(skinElement.getAsJsonObject().get("url"))
        .map(JsonElement::getAsString)
        .orElse(null);
  }

  public static int getSlotFromRowCol(final int row, final int col) {
    return (col + (row - 1) * 9) - 1;
  }
}
