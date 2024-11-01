package dev.varion.glim;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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

  public void itemStack(final ItemStack itemStack) {
    if ( Objects.isNull(itemStack) || itemStack.getType().isAir()) {
      this.itemStack = new ItemStack(Material.AIR);
      return;
    }

    final ItemStack newItemStack = itemStack.clone();
    Glim.setNbt(newItemStack, "glim", uniqueId.toString(), PersistentDataType.STRING);
    this.itemStack = newItemStack;
  }

  public UUID uniqueId() {
    return uniqueId;
  }

  public Consumer<InventoryClickEvent> action() {
    return action;
  }

  public void action(final Consumer<InventoryClickEvent> action) {
    this.action = action;
  }
}
