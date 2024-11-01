package dev.varion.glim;

import static dev.varion.glim.GlimItemUtils.setNbt;
import static org.bukkit.persistence.PersistentDataType.STRING;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GlimItem {

  private final UUID uniqueId = UUID.randomUUID();
  private ItemStack itemStack;
  private Consumer<InventoryClickEvent> action;

  public GlimItem(final ItemStack itemStack, final Consumer<InventoryClickEvent> action) {
    this.action = action;
    itemStack(itemStack);
  }

  public GlimItem(final ItemStack itemStack) {
    this(itemStack, null);
  }

  public ItemStack itemStack() {
    return itemStack;
  }

  public GlimItem itemStack(final ItemStack itemStack) {
    if (Objects.isNull(itemStack) || itemStack.getType().isAir()) {
      this.itemStack = new ItemStack(Material.AIR);
      return this;
    }

    final ItemStack newItemStack = itemStack.clone();
    final ItemMeta itemMeta = newItemStack.getItemMeta();
    setNbt("glim", uniqueId.toString(), STRING).accept(itemMeta.getPersistentDataContainer());
    newItemStack.setItemMeta(itemMeta);
    this.itemStack = newItemStack;
    return this;
  }

  public UUID uniqueId() {
    return uniqueId;
  }

  public Consumer<InventoryClickEvent> action() {
    return action;
  }

  public GlimItem action(final Consumer<InventoryClickEvent> action) {
    this.action = action;
    return this;
  }
}
