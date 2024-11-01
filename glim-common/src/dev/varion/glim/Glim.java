package dev.varion.glim;

import static java.util.Optional.ofNullable;
import static org.bukkit.Bukkit.getPluginManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.varion.glim.gui.GuiController;
import dev.varion.glim.gui.GuiType;
import dev.varion.glim.gui.SimpleGuiCreator;
import dev.varion.glim.gui.editor.EditorGuiCreator;
import dev.varion.glim.gui.paginated.PaginatedGuiCreator;
import dev.varion.glim.modifier.InteractionModifierController;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class Glim {

  private static final Gson GSON = new Gson();
  private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(Glim.class);

  private Glim() {}

  public static Glim create() {
    getPluginManager().registerEvents(new GuiController(), PLUGIN);
    getPluginManager().registerEvents(new InteractionModifierController(), PLUGIN);
    return new Glim();
  }

  public static String getSkinUrl(final String texture) {
    final String decoded = new String(Base64.getDecoder().decode(texture), StandardCharsets.UTF_8);
    final JsonObject object = GSON.fromJson(decoded, JsonObject.class);

    final JsonElement textures = object.get("textures");
    if (Objects.isNull(textures)) {
      return null;
    }

    final JsonElement skin = textures.getAsJsonObject().get("SKIN");
    if (Objects.isNull(skin)) {
      return null;
    }

    return ofNullable(skin.getAsJsonObject().get("url")).map(JsonElement::getAsString).orElse(null);
  }

  public static <K, T> void setNbt(
      final ItemStack itemStack,
      final String key,
      final T value,
      final PersistentDataType<K, T> dataType) {
    final ItemMeta meta = itemStack.getItemMeta();
    if (Objects.isNull(meta)) return;
    meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), dataType, value);
    itemStack.setItemMeta(meta);
  }

  public static void removeNbt(final ItemStack itemStack, final String key) {
    final ItemMeta meta = itemStack.getItemMeta();
    if (Objects.isNull(meta)) return;
    meta.getPersistentDataContainer().remove(new NamespacedKey(PLUGIN, key));
    itemStack.setItemMeta(meta);
  }

  public static <K, T> T retrieveNbt(
      final ItemStack itemStack, final String key, final PersistentDataType<K, T> dataType) {
    final ItemMeta meta = itemStack.getItemMeta();
    if (Objects.isNull(meta)) return null;
    return meta.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, key), dataType);
  }

  public static int getSlotFromRowCol(final int row, final int col) {
    return (col + (row - 1) * 9) - 1;
  }

  public SimpleGuiCreator creator(final GuiType type) {
    return new SimpleGuiCreator(type);
  }

  public SimpleGuiCreator creator() {
    return creator(GuiType.CHEST);
  }

  public EditorGuiCreator editorCreator() {
    return new EditorGuiCreator();
  }

  public PaginatedGuiCreator paginatedCreator() {
    return new PaginatedGuiCreator();
  }
}
