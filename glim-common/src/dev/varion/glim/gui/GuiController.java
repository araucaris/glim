package dev.varion.glim.gui;

import static dev.varion.glim.GlimItemUtils.retrieveNbt;
import static java.util.Optional.ofNullable;
import static org.bukkit.event.inventory.InventoryType.PLAYER;
import static org.bukkit.persistence.PersistentDataType.STRING;

import dev.varion.glim.GlimItem;
import dev.varion.glim.gui.paginated.PaginatedGui;
import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public final class GuiController implements Listener {

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    final Consumer<InventoryClickEvent> outsideClickAction = gui.outsideClickAction();
    if (Objects.nonNull(outsideClickAction) && Objects.isNull(event.getClickedInventory())) {
      outsideClickAction.accept(event);
      return;
    }

    if (event.getClickedInventory() == null) return;

    final Consumer<InventoryClickEvent> defaultTopClick = gui.defaultTopClickAction();
    if (Objects.nonNull(defaultTopClick)
        && !Objects.equals(event.getClickedInventory().getType(), PLAYER)) {
      defaultTopClick.accept(event);
    }

    final Consumer<InventoryClickEvent> playerInventoryClick = gui.playerInventoryAction();
    if (Objects.nonNull(playerInventoryClick)
        && Objects.equals(event.getClickedInventory().getType(), PLAYER)) {
      playerInventoryClick.accept(event);
    }

    final Consumer<InventoryClickEvent> defaultClick = gui.defaultClickAction();
    if (Objects.nonNull(defaultClick)) defaultClick.accept(event);

    final Consumer<InventoryClickEvent> slotAction = gui.action(event.getSlot());
    if (Objects.nonNull(slotAction)
        && !Objects.equals(event.getClickedInventory().getType(), PLAYER)) {
      slotAction.accept(event);
    }

    GlimItem item;
    if (gui instanceof final PaginatedGui paginatedGui) {
      item = paginatedGui.item(event.getSlot());
      if (item == null) {
        item = paginatedGui.item(event.getSlot());
      }
    } else {
      item = gui.item(event.getSlot());
    }

    if (!isGuiItem(event.getCurrentItem(), item)) return;

    final Consumer<InventoryClickEvent> itemAction = item.action();
    if (Objects.nonNull(itemAction)) itemAction.accept(event);
  }

  @EventHandler
  public void onDrag(final InventoryDragEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    final Consumer<InventoryDragEvent> dragAction = gui.dragAction();
    if (Objects.nonNull(dragAction)) dragAction.accept(event);
  }

  @EventHandler
  public void onOpen(final InventoryOpenEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    final Consumer<InventoryOpenEvent> openAction = gui.openGuiAction();
    if (Objects.nonNull(openAction) && !gui.isBeingUpdated()) openAction.accept(event);
  }

  @EventHandler
  public void onClose(final InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    final Consumer<InventoryCloseEvent> closeAction = gui.closeGuiAction();
    if (Objects.nonNull(closeAction) && !gui.isBeingUpdated()) closeAction.accept(event);
  }

  private boolean isGuiItem(final ItemStack itemStack, final GlimItem glimItem) {
    if (Objects.isNull(itemStack) || Objects.isNull(glimItem)) return false;
    if (!itemStack.hasItemMeta()) {
      return false;
    }
    return ofNullable(
            retrieveNbt("glim", STRING).apply(itemStack.getItemMeta().getPersistentDataContainer()))
        .filter(nbt -> Objects.equals(nbt, glimItem.uniqueId().toString()))
        .isPresent();
  }
}
