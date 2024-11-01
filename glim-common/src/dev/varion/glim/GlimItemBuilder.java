package dev.varion.glim;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.varion.glim.gui.GuiUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;

public final class GlimItemBuilder {

  private static final String DEFAULT_PROFILE_NAME = "";
  private final ItemStack itemStack;
  private final ItemMeta itemMeta;

  private GlimItemBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    itemMeta =
        itemStack.hasItemMeta()
            ? itemStack.getItemMeta()
            : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
  }

  public static GlimItemBuilder from(final ItemStack itemStack) {
    return new GlimItemBuilder(itemStack);
  }

  public static GlimItemBuilder from(final Material material) {
    return new GlimItemBuilder(new ItemStack(material));
  }

  public static GlimItemBuilder skull() {
    return from(Material.PLAYER_HEAD);
  }

  public GlimItemBuilder name(final Component name) {
    if (Objects.nonNull(itemMeta)) {
      itemMeta.displayName(name);
    }
    return this;
  }

  public GlimItemBuilder amount(final int amount) {
    itemStack.setAmount(amount);
    return this;
  }

  public GlimItemBuilder lore(final Component... lore) {
    return lore(Arrays.asList(lore));
  }

  public GlimItemBuilder lore(final List<Component> lore) {
    if (Objects.nonNull(itemMeta)) {
      itemMeta.lore(lore);
    }
    return this;
  }

  public GlimItemBuilder lore(final Consumer<List<Component>> mutator) {
    if (Objects.nonNull(itemMeta)) {
      final List<Component> itemLore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
      mutator.accept(itemLore);
      return lore(itemLore);
    }
    return this;
  }

  public GlimItemBuilder enchant(
      final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
    itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
    return this;
  }

  public GlimItemBuilder enchant(final Enchantment enchantment, final int level) {
    return enchant(enchantment, level, true);
  }

  public GlimItemBuilder enchant(final Enchantment enchantment) {
    return enchant(enchantment, 1, true);
  }

  public GlimItemBuilder enchant(
      final Map<Enchantment, Integer> enchantments, final boolean ignoreLevelRestriction) {
    enchantments.forEach(
        (enchantment, level) -> enchant(enchantment, level, ignoreLevelRestriction));
    return this;
  }

  public GlimItemBuilder enchant(final Map<Enchantment, Integer> enchantments) {
    return enchant(enchantments, true);
  }

  public GlimItemBuilder removeEnchant(final Enchantment enchantment) {
    itemStack.removeEnchantment(enchantment);
    return this;
  }

  public GlimItemBuilder flags(final ItemFlag... flags) {
    itemMeta.addItemFlags(flags);
    return this;
  }

  public GlimItemBuilder unbreakable() {
    return unbreakable(true);
  }

  public GlimItemBuilder unbreakable(final boolean unbreakable) {
    itemMeta.setUnbreakable(unbreakable);
    return this;
  }

  public GlimItemBuilder glow() {
    return glow(true);
  }

  public GlimItemBuilder glow(final boolean glow) {
    if (glow) {
      itemMeta.addEnchant(Enchantment.LURE, 1, false);
      itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    } else {
      itemMeta.getEnchants().keySet().forEach(itemMeta::removeEnchant);
    }
    return this;
  }

  public GlimItemBuilder pdc(final Consumer<PersistentDataContainer> dataUpdater) {
    itemStack.setItemMeta(itemMeta);
    dataUpdater.accept(itemMeta.getPersistentDataContainer());
    return this;
  }

  public GlimItemBuilder customModelData(final int modelData) {
    itemMeta.setCustomModelData(modelData);
    return this;
  }

  public <K, T> GlimItemBuilder setNbt(
      final String key, final T value, final PersistentDataType<K, T> dataType) {
    return pdc(GlimItemUtils.setNbt(key, value, dataType));
  }

  public GlimItemBuilder removeNbt(final String key) {
    return pdc(GlimItemUtils.removeNbt(key));
  }

  public GlimItemBuilder texture(final String texture, final UUID uniqueId) {
    if (Objects.equals(itemStack.getType(), Material.PLAYER_HEAD)) {
      applyTexture(texture, uniqueId);
    }
    return this;
  }

  public GlimItemBuilder texture(final String texture) {
    return texture(texture, UUID.randomUUID());
  }

  public GlimItemBuilder owner(final OfflinePlayer player) {
    if (Objects.equals(itemStack.getType(), Material.PLAYER_HEAD)) {
      ((SkullMeta) itemMeta).setOwningPlayer(player);
    }
    return this;
  }

  public ItemStack asItemStack() {
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public GlimItem asGlim() {
    return new GlimItem(asItemStack());
  }

  public GlimItem asGlim(final Consumer<InventoryClickEvent> action) {
    return new GlimItem(asItemStack(), action);
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private void applyTexture(final String texture, final UUID uniqueId) {
    final String textureUrl = GuiUtils.getSkinUrl(texture);
    if (textureUrl != null) {
      final SkullMeta skullMeta = (SkullMeta) itemMeta;
      final PlayerProfile profile = Bukkit.createProfile(uniqueId, DEFAULT_PROFILE_NAME);
      final PlayerTextures textures = profile.getTextures();
      try {
        textures.setSkin(new URL(textureUrl));
      } catch (final MalformedURLException exception) {
        exception.printStackTrace();
      }
      profile.setTextures(textures);
      skullMeta.setPlayerProfile(profile);
    }
  }
}
