package dev.varion.glim;

import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class GlimItemUtils {

  private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(Glim.class);

  private GlimItemUtils() {

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
}
