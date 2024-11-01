package dev.varion.glim.modifier;

import static dev.varion.glim.modifier.InteractionModifier.ITEM_DROP;
import static dev.varion.glim.modifier.InteractionModifier.ITEM_PLACE;
import static dev.varion.glim.modifier.InteractionModifier.ITEM_SWAP;
import static dev.varion.glim.modifier.InteractionModifier.ITEM_TAKE;
import static dev.varion.glim.modifier.InteractionModifier.OTHER_ACTIONS;

import dev.varion.glim.gui.Gui;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class InteractionModifierController implements Listener {

  private static final Set<InventoryAction> ITEM_TAKE_ACTIONS =
      EnumSet.of(
          InventoryAction.PICKUP_ONE,
          InventoryAction.PICKUP_SOME,
          InventoryAction.PICKUP_HALF,
          InventoryAction.PICKUP_ALL,
          InventoryAction.COLLECT_TO_CURSOR,
          InventoryAction.HOTBAR_SWAP,
          InventoryAction.MOVE_TO_OTHER_INVENTORY);

  private static final Set<InventoryAction> ITEM_PLACE_ACTIONS =
      EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL);

  private static final Set<InventoryAction> ITEM_SWAP_ACTIONS =
      EnumSet.of(
          InventoryAction.HOTBAR_SWAP,
          InventoryAction.SWAP_WITH_CURSOR,
          InventoryAction.HOTBAR_MOVE_AND_READD);

  private static final Set<InventoryAction> ITEM_DROP_ACTIONS =
      EnumSet.of(
          InventoryAction.DROP_ONE_SLOT,
          InventoryAction.DROP_ALL_SLOT,
          InventoryAction.DROP_ONE_CURSOR,
          InventoryAction.DROP_ALL_CURSOR);

  @EventHandler
  public void onGuiClick(final InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    if (gui.allInteractionsDisabled()) {
      event.setCancelled(true);
      event.setResult(Event.Result.DENY);
      return;
    }

    if ((!gui.can(ITEM_PLACE) && isPlaceItemEvent(event))
        || (!gui.can(ITEM_TAKE) && isTakeItemEvent(event))
        || (!gui.can(ITEM_SWAP) && isSwapItemEvent(event))
        || (!gui.can(ITEM_DROP) && isDropItemEvent(event))
        || (!gui.can(OTHER_ACTIONS) && isOtherEvent(event))) {
      event.setCancelled(true);
      event.setResult(Event.Result.DENY);
    }
  }

  @EventHandler
  public void onGuiDrag(final InventoryDragEvent event) {
    if (!(event.getInventory().getHolder() instanceof final Gui gui)) return;

    if (gui.allInteractionsDisabled()) {
      event.setCancelled(true);
      event.setResult(Event.Result.DENY);
      return;
    }

    if (gui.can(ITEM_PLACE) || !isDraggingOnGui(event)) return;

    event.setCancelled(true);
    event.setResult(Event.Result.DENY);
  }

  private boolean isTakeItemEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final Inventory clickedInventory = event.getClickedInventory();
    final InventoryAction action = event.getAction();

    if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER
        || inventory.getType() == InventoryType.PLAYER) {
      return false;
    }

    return action == InventoryAction.MOVE_TO_OTHER_INVENTORY || ITEM_TAKE_ACTIONS.contains(action);
  }

  private boolean isPlaceItemEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final Inventory clickedInventory = event.getClickedInventory();
    final InventoryAction action = event.getAction();

    if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY
        && clickedInventory != null
        && clickedInventory.getType() == InventoryType.PLAYER
        && inventory.getType() != clickedInventory.getType()) {
      return true;
    }

    return ITEM_PLACE_ACTIONS.contains(action)
        && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
        && inventory.getType() != InventoryType.PLAYER;
  }

  private boolean isSwapItemEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final Inventory clickedInventory = event.getClickedInventory();
    final InventoryAction action = event.getAction();

    return ITEM_SWAP_ACTIONS.contains(action)
        && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
        && inventory.getType() != InventoryType.PLAYER;
  }

  private boolean isDropItemEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final Inventory clickedInventory = event.getClickedInventory();
    final InventoryAction action = event.getAction();

    return ITEM_DROP_ACTIONS.contains(action)
        && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
  }

  private boolean isOtherEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final Inventory clickedInventory = event.getClickedInventory();
    final InventoryAction action = event.getAction();

    return isOtherAction(action)
        && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
  }

  private boolean isDraggingOnGui(final InventoryDragEvent event) {
    final int topSlots = event.getView().getTopInventory().getSize();
    return event.getRawSlots().stream().anyMatch(slot -> slot < topSlots);
  }

  private boolean isOtherAction(final InventoryAction action) {
    return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
  }
}
