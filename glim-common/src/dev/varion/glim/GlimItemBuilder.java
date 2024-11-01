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

public class GlimItemBuilder {

  private final ItemStack itemStack;
  private ItemMeta meta;

  GlimItemBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    meta =
        itemStack.hasItemMeta()
            ? itemStack.getItemMeta()
            : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
  }

  public static GlimItemBuilder of(final ItemStack itemStack) {
    return new GlimItemBuilder(itemStack);
  }

  public static GlimItemBuilder of(final Material material) {
    return new GlimItemBuilder(new ItemStack(material));
  }

  public static GlimItemBuilder skull() {
    return of(Material.PLAYER_HEAD);
  }

  public GlimItemBuilder name(final Component name) {
    if (Objects.isNull(meta)) return this;
    meta.displayName(name);
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
    if (Objects.isNull(meta)) return this;
    meta.lore(lore);
    return this;
  }

  public GlimItemBuilder lore(final Consumer<List<Component>> mutator) {
    if (Objects.isNull(meta)) return this;
    final List<Component> itemLore = meta.hasLore() ? meta.lore() : new ArrayList<>();
    mutator.accept(itemLore);
    return lore(itemLore);
  }

  public GlimItemBuilder enchant(
      final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
    meta.addEnchant(enchantment, level, ignoreLevelRestriction);
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

  public GlimItemBuilder disenchant(final Enchantment enchantment) {
    itemStack.removeEnchantment(enchantment);
    return this;
  }

  public GlimItemBuilder flags(final ItemFlag... flags) {
    meta.addItemFlags(flags);
    return this;
  }

  public GlimItemBuilder unbreakable() {
    return unbreakable(true);
  }

  public GlimItemBuilder unbreakable(final boolean unbreakable) {
    meta.setUnbreakable(unbreakable);
    return this;
  }

  public GlimItemBuilder glow() {
    return glow(true);
  }

  public GlimItemBuilder glow(final boolean glow) {
    if (glow) {
      meta.addEnchant(Enchantment.LURE, 1, false);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      return this;
    }

    for (final Enchantment enchantment : meta.getEnchants().keySet()) {
      meta.removeEnchant(enchantment);
    }

    return this;
  }

  public GlimItemBuilder pdc(final Consumer<PersistentDataContainer> consumer) {
    itemStack.setItemMeta(meta);
    consumer.accept(meta.getPersistentDataContainer());
    return this;
  }

  public GlimItemBuilder model(final int modelData) {
    meta.setCustomModelData(modelData);
    return this;
  }

  public <K, T> GlimItemBuilder setNbt(
      final String key, final T value, final PersistentDataType<K, T> dataType) {
    return pdc(pdc -> GlimItemUtils.setNbt(key, value, dataType).accept(pdc));
  }

  public GlimItemBuilder removeNbt(final String key) {
    return pdc(pdc -> GlimItemUtils.removeNbt(key).accept(pdc));
  }

  public GlimItemBuilder texture(final String texture, final UUID profileId) {
    if (!Objects.equals(itemStack.getType(), Material.PLAYER_HEAD)) {
      return this;
    }

    final String textureUrl = GuiUtils.getSkinUrl(texture);
    if (Objects.isNull(textureUrl)) {
      return this;
    }

    final SkullMeta skullMeta = (SkullMeta) meta;
    final PlayerProfile profile = Bukkit.createProfile(profileId, "");
    final PlayerTextures textures = profile.getTextures();

    try {
      textures.setSkin(new URL(textureUrl));
    } catch (final MalformedURLException exception) {
      exception.printStackTrace();
      return this;
    }

    profile.setTextures(textures);
    skullMeta.setPlayerProfile(profile);
    return this;
  }

  public GlimItemBuilder texture(final String texture) {
    return texture(texture, UUID.randomUUID());
  }

  public GlimItemBuilder owner(final OfflinePlayer player) {
    if (!Objects.equals(itemStack.getType(), Material.PLAYER_HEAD)) return this;

    final SkullMeta skullMeta = (SkullMeta) meta;
    skullMeta.setOwningPlayer(player);
    return this;
  }

  public ItemStack asItemStack() {
    itemStack.setItemMeta(meta);
    return itemStack;
  }

  public GlimItem asGlim() {
    return new GlimItem(asItemStack());
  }

  public GlimItem asGlim(final Consumer<InventoryClickEvent> action) {
    return new GlimItem(asItemStack(), action);
  }
}
