package dev.varion.glim.gui;

import dev.varion.glim.Glim;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class GuiController implements Listener {

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) {
      return;
    }

    final Consumer<InventoryClickEvent> outsideClickAction = gui.outsideClickAction();
    if (outsideClickAction != null && event.getClickedInventory() == null) {
      outsideClickAction.accept(event);
      return;
    }

    if (event.getClickedInventory() == null) {
      return;
    }

    final Consumer<InventoryClickEvent> defaultTopClick = gui.defaultTopClickAction();
    if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
      defaultTopClick.accept(event);
    }

    final Consumer<InventoryClickEvent> playerInventoryClick = gui.playerInventoryAction();
    if (playerInventoryClick != null
        && event.getClickedInventory().getType() == InventoryType.PLAYER) {
      playerInventoryClick.accept(event);
    }

    final Consumer<InventoryClickEvent> defaultClick = gui.defaultClickAction();
    if (defaultClick != null) {
      defaultClick.accept(event);
    }

    final Consumer<InventoryClickEvent> slotAction = gui.slotAction(event.getSlot());
    if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
      slotAction.accept(event);
    }

    GlimItem item;
    if (gui instanceof final PaginatedGui paginatedGui) {
      item = paginatedGui.guiItem(event.getSlot());
      if (item == null) {
        item = paginatedGui.pageItem(event.getSlot());
      }
    } else {
      item = gui.guiItem(event.getSlot());
    }

    if (!isGuiItem(event.getCurrentItem(), item)) {
      return;
    }

    final Consumer<InventoryClickEvent> itemAction = item.action();
    if (itemAction != null) {
      itemAction.accept(event);
    }
  }

  @EventHandler
  public void onDrag(final InventoryDragEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) {
      return;
    }

    final Consumer<InventoryDragEvent> dragAction = gui.dragAction();
    if (dragAction != null) {
      dragAction.accept(event);
    }
  }

  @EventHandler
  public void onOpen(final InventoryOpenEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) {
      return;
    }

    final Consumer<InventoryOpenEvent> openAction = gui.openGuiAction();
    if (openAction != null && !gui.isBeingUpdated()) {
      openAction.accept(event);
    }
  }

  @EventHandler
  public void onClose(final InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) {
      return;
    }

    final Consumer<InventoryCloseEvent> closeAction = gui.closeGuiAction();
    if (closeAction != null && !gui.isBeingUpdated()) {
      closeAction.accept(event);
    }
  }

  private boolean isGuiItem(final ItemStack itemStack, final GlimItem glimItem) {
    if (itemStack == null || glimItem == null) {
      return false;
    }

    final String nbt = Glim.retrieveNbt(itemStack, "mf-gui", PersistentDataType.STRING);
    if (nbt == null) {
      return false;
    }
    return Objects.equals(nbt, glimItem.uniqueId().toString());
  }
}
