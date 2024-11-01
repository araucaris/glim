package dev.varion.glim.gui;

import static dev.varion.glim.gui.GuiUtils.getSlotFromRowCol;
import static java.util.Collections.unmodifiableCollection;

import dev.varion.glim.GlimException;
import dev.varion.glim.GlimItem;
import dev.varion.glim.modifier.InteractionModifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Gui implements InventoryHolder {

  private final GuiFiller filler;
  private final Map<Integer, GlimItem> itemsBySlot;
  private final Map<Integer, Consumer<InventoryClickEvent>> actionsBySlot;
  private final Set<InteractionModifier> interactionModifiers;
  private Inventory inventory;
  private Component title;
  private int rows;
  private GuiType guiType;
  private boolean updating;
  private Consumer<InventoryClickEvent> defaultClickAction;
  private Consumer<InventoryClickEvent> defaultTopClickAction;
  private Consumer<InventoryClickEvent> playerInventoryAction;
  private Consumer<InventoryDragEvent> dragAction;
  private Consumer<InventoryCloseEvent> closeGuiAction;
  private Consumer<InventoryOpenEvent> openGuiAction;
  private Consumer<InventoryClickEvent> outsideClickAction;

  protected Gui(
      final int rows, final Component title, final Set<InteractionModifier> interactionModifiers) {
    this(!(rows >= 1 && rows <= 6) ? 1 : rows, GuiType.CHEST, title, interactionModifiers);
  }

  protected Gui(
      final GuiType guiType,
      final Component title,
      final Set<InteractionModifier> interactionModifiers) {
    this(1, guiType, title, interactionModifiers);
  }

  protected Gui(
      final int rows,
      final GuiType guiType,
      final Component title,
      final Set<InteractionModifier> interactionModifiers) {
    this.rows = rows;
    this.guiType = guiType;
    this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    this.interactionModifiers.addAll(interactionModifiers);
    this.title = title;
    final boolean isChest = Objects.equals(guiType, GuiType.CHEST);
    final int inventorySize = isChest ? rows * 9 : guiType.limit();
    inventory =
        isChest
            ? Bukkit.createInventory(this, inventorySize, title)
            : Bukkit.createInventory(this, guiType.inventoryType(), title);
    actionsBySlot = new LinkedHashMap<>(inventorySize);
    itemsBySlot = new LinkedHashMap<>(inventorySize);
    filler = new GuiFiller(this);
  }

  public void set(final int slot, final GlimItem item) {
    validateSlot(slot);
    itemsBySlot.put(slot, item);
  }

  public void remove(final int slot) {
    validateSlot(slot);
    itemsBySlot.remove(slot);
    inventory.setItem(slot, null);
  }

  public void remove(final int row, final int col) {
    remove(getSlotFromRowCol(row, col));
  }

  public void remove(final GlimItem item) {
    iterate(it -> it.getValue().equals(item), it -> remove(it.getKey()));
  }

  public void iterate(
      final Predicate<Map.Entry<Integer, GlimItem>> predicate,
      final Consumer<Map.Entry<Integer, GlimItem>> action) {
    itemsBySlot.entrySet().stream().filter(predicate).forEach(action);
  }

  public void set(final List<Integer> slots, final GlimItem item) {
    slots.forEach(slot -> set(slot, item));
  }

  public void set(final int row, final int col, final GlimItem item) {
    set(getSlotFromRowCol(row, col), item);
  }

  public void insert(final GlimItem... items) {
    insert(List.of(items));
  }

  public void insert(final Collection<GlimItem> items) {
    insert(false, items);
  }

  public void insert(final boolean expandIfFull, final Collection<GlimItem> items) {
    final List<GlimItem> notAddedItems = new ArrayList<>();

    for (final GlimItem item : items) {
      for (int slot = 0; slot < rows * 9; slot++) {
        if (itemsBySlot.get(slot) != null) {
          if (slot == rows * 9 - 1) {
            notAddedItems.add(item);
          }
          continue;
        }

        itemsBySlot.put(slot, item);
        break;
      }
    }

    if (!expandIfFull
        || rows >= 6
        || notAddedItems.isEmpty()
        || (guiType != null && guiType != GuiType.CHEST)) {
      return;
    }

    rows++;
    inventory = Bukkit.createInventory(this, rows * 9, title);
    update();
    insert(true, notAddedItems);
  }

  public void update(final int slot, final ItemStack itemStack) {
    final GlimItem item = itemsBySlot.get(slot);
    if (item == null) {
      update(slot, new GlimItem(itemStack));
      return;
    }

    item.itemStack(itemStack);
    update(slot, item);
  }

  public void update(final int row, final int col, final ItemStack itemStack) {
    update(getSlotFromRowCol(row, col), itemStack);
  }

  public void update(final int slot, final GlimItem item) {
    itemsBySlot.put(slot, item);
    inventory.setItem(slot, item.itemStack());
  }

  public void update(final int row, final int col, final GlimItem item) {
    update(getSlotFromRowCol(row, col), item);
  }

  public boolean contains(final int slot) {
    return itemsBySlot.containsKey(slot);
  }

  public GlimItem item(final int slot) {
    return itemsBySlot.get(slot);
  }

  public void action(final int slot, final Consumer<InventoryClickEvent> slotAction) {
    validateSlot(slot);
    actionsBySlot.put(slot, slotAction);
  }

  public void action(final int row, final int col, final Consumer<InventoryClickEvent> slotAction) {
    action(getSlotFromRowCol(row, col), slotAction);
  }

  public Consumer<InventoryClickEvent> action(final int slot) {
    return actionsBySlot.get(slot);
  }

  public Gui disable(final InteractionModifier modifier) {
    interactionModifiers.remove(modifier);
    return this;
  }

  public Gui disableAllInteractions() {
    interactionModifiers.clear();
    return this;
  }

  public Gui allow(final InteractionModifier modifier) {
    interactionModifiers.add(modifier);
    return this;
  }

  public Gui allowAll() {
    interactionModifiers.addAll(List.of(InteractionModifier.values()));
    return this;
  }

  public boolean can(final InteractionModifier modifier) {
    return interactionModifiers.contains(modifier);
  }

  public boolean allInteractionsDisabled() {
    return interactionModifiers.isEmpty();
  }

  public void populateGui() {
    itemsBySlot.forEach((key, value) -> inventory.setItem(key, value.itemStack()));
  }

  public Gui updateTitle(final Component title) {
    updating = true;

    final Collection<HumanEntity> viewersBeforeUpdate =
        unmodifiableCollection(inventory.getViewers());

    inventory = Bukkit.createInventory(this, inventory.getSize(), title);

    viewersBeforeUpdate.forEach(this::open);

    updating = false;
    this.title = title;
    return this;
  }

  public void open(final HumanEntity player) {
    if (player.isSleeping()) return;

    inventory.clear();
    populateGui();
    player.openInventory(inventory);
  }

  public void update() {
    inventory.clear();
    populateGui();
    unmodifiableCollection(inventory.getViewers())
        .forEach(viewer -> ((Player) viewer).updateInventory());
  }

  private void validateSlot(final int slot) {
    final int limit = guiType.limit();

    if (guiType == GuiType.CHEST) {
      if (slot < 0 || slot >= rows * limit) {
        throw new GlimException(
            "Slot %d is not valid for %s and rows %d".formatted(slot, guiType.name(), rows));
      }
      return;
    }

    if (slot < 0 || slot > limit) {
      throw new GlimException("Slot %d is not valid for %s".formatted(slot, guiType.name()));
    }
  }

  public Component title() {
    return title;
  }

  public GuiFiller filler() {
    return filler;
  }

  public Map<Integer, Consumer<InventoryClickEvent>> slotActions() {
    return actionsBySlot;
  }

  public Set<InteractionModifier> interactionModifiers() {
    return interactionModifiers;
  }

  public Inventory inventory() {
    return inventory;
  }

  public void inventory(final Inventory inventory) {
    this.inventory = inventory;
  }

  public void title(final Component title) {
    this.title = title;
  }

  public int rows() {
    return rows;
  }

  public void rows(final int rows) {
    this.rows = rows;
  }

  public GuiType guiType() {
    return guiType;
  }

  public void guiType(final GuiType guiType) {
    this.guiType = guiType;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isBeingUpdated() {
    return updating;
  }

  public void markAsBeingUpdated() {
    updating = true;
  }

  public void markAsUpdated() {
    updating = false;
  }

  public Consumer<InventoryClickEvent> defaultClickAction() {
    return defaultClickAction;
  }

  public void defaultClickAction(final Consumer<InventoryClickEvent> defaultClickAction) {
    this.defaultClickAction = defaultClickAction;
  }

  public Consumer<InventoryClickEvent> defaultTopClickAction() {
    return defaultTopClickAction;
  }

  public void defaultTopClickAction(final Consumer<InventoryClickEvent> defaultTopClickAction) {
    this.defaultTopClickAction = defaultTopClickAction;
  }

  public Consumer<InventoryClickEvent> playerInventoryAction() {
    return playerInventoryAction;
  }

  public void playerInventoryAction(final Consumer<InventoryClickEvent> playerInventoryAction) {
    this.playerInventoryAction = playerInventoryAction;
  }

  public Consumer<InventoryDragEvent> dragAction() {
    return dragAction;
  }

  public void dragAction(final Consumer<InventoryDragEvent> dragAction) {
    this.dragAction = dragAction;
  }

  public Consumer<InventoryCloseEvent> closeGuiAction() {
    return closeGuiAction;
  }

  public void closeGuiAction(final Consumer<InventoryCloseEvent> closeGuiAction) {
    this.closeGuiAction = closeGuiAction;
  }

  public Consumer<InventoryOpenEvent> openGuiAction() {
    return openGuiAction;
  }

  public void openGuiAction(final Consumer<InventoryOpenEvent> openGuiAction) {
    this.openGuiAction = openGuiAction;
  }

  public Consumer<InventoryClickEvent> outsideClickAction() {
    return outsideClickAction;
  }

  public void outsideClickAction(final Consumer<InventoryClickEvent> outsideClickAction) {
    this.outsideClickAction = outsideClickAction;
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
