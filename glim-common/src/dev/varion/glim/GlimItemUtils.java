package dev.varion.glim;

import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class GlimItemUtils {

  private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(Glim.class);

  private GlimItemUtils() {}

  public static <K, T> Consumer<PersistentDataContainer> setNbt(
      final String key, final T value, final PersistentDataType<K, T> dataType) {
    return pdc -> pdc.set(new NamespacedKey(PLUGIN, key), dataType, value);
  }

  public static Consumer<PersistentDataContainer> removeNbt(final String key) {
    return pdc -> pdc.remove(new NamespacedKey(PLUGIN, key));
  }

  public static <K, T> Function<PersistentDataContainer, T> retrieveNbt(
      final String key, final PersistentDataType<K, T> dataType) {
    return pdc -> pdc.get(new NamespacedKey(PLUGIN, key), dataType);
  }
}
