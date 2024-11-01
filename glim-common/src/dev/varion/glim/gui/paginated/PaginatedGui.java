package dev.varion.glim.gui.paginated;

import static java.util.Collections.unmodifiableMap;

import dev.varion.glim.GlimItem;
import dev.varion.glim.gui.Gui;
import dev.varion.glim.modifier.InteractionModifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public final class PaginatedGui extends Gui {

  private final List<GlimItem> pageItems;
  private final Map<Integer, GlimItem> currentItemsBySlot;

  private int pageSize;
  private int pageNum = 1;

  public PaginatedGui(
      final int rows,
      final int pageSize,
      final Component title,
      final Set<InteractionModifier> interactionModifiers) {
    super(rows, title, interactionModifiers);
    this.pageSize = pageSize;
    pageItems = new ArrayList<>();
    currentItemsBySlot = new LinkedHashMap<>(rows * 9);
  }

  public Gui pageSize(final int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  @Override
  public void insert(final boolean expandIfFull, final Collection<GlimItem> items) {
    pageItems.addAll(items);
  }

  @Override
  public void update() {
    getInventory().clear();
    populateGui();
    updatePage();
  }

  @Override
  public void update(final int slot, final ItemStack itemStack) {
    if (!currentItemsBySlot.containsKey(slot)) {
      super.update(slot, itemStack);
      return;
    }

    final GlimItem item = currentItemsBySlot.get(slot);
    item.itemStack(itemStack);
    getInventory().setItem(slot, item.itemStack());
  }

  @Override
  public void update(final int slot, final GlimItem item) {
    if (!currentItemsBySlot.containsKey(slot)) return;

    final GlimItem oldItem = currentItemsBySlot.get(slot);
    final int index = pageItems.indexOf(oldItem);

    currentItemsBySlot.put(slot, item);
    pageItems.set(index, item);
    getInventory().setItem(slot, item.itemStack());
  }

  @Override
  public void remove(final GlimItem item) {
    final boolean removed = pageItems.remove(item);
    if (!removed) {
      super.remove(item);
    }

    updatePage();
  }

  @Override
  public void iterate(
      final Predicate<Map.Entry<Integer, GlimItem>> predicate,
      final Consumer<Map.Entry<Integer, GlimItem>> action) {
    currentItemsBySlot.entrySet().stream().filter(predicate).forEach(action);
  }

  @Override
  public GlimItem item(final int slot) {
    final GlimItem item = currentItemsBySlot.get(slot);
    if (item == null) {
      return super.item(slot);
    }
    return item;
  }

  @Override
  public Gui updateTitle(final Component title) {
    markAsBeingUpdated();

    final List<HumanEntity> viewers = new ArrayList<>(getInventory().getViewers());

    inventory(Bukkit.createInventory(this, getInventory().getSize(), title));

    for (final HumanEntity player : viewers) {
      open(player, pageNum());
    }

    markAsUpdated();

    return this;
  }

  @Override
  public void open(final HumanEntity player) {
    open(player, 1);
  }

  public void open(final HumanEntity player, final int openPage) {
    if (player.isSleeping()) return;
    if (openPage <= pagesCount() || openPage > 0) pageNum = openPage;

    getInventory().clear();
    currentItemsBySlot.clear();

    populateGui();

    if (pageSize == 0) pageSize = calculatePageSize();

    populatePage();

    player.openInventory(getInventory());
  }

  public boolean next() {
    if (pageNum + 1 > pagesCount()) return false;

    pageNum++;
    updatePage();
    return true;
  }

  public boolean previous() {
    if (pageNum - 1 == 0) return false;

    pageNum--;
    updatePage();
    return true;
  }

  public int nextPageNum() {
    if (pageNum + 1 > pagesCount()) return pageNum;
    return pageNum + 1;
  }

  public int previousPageNum() {
    if (pageNum - 1 == 0) return pageNum;
    return pageNum - 1;
  }

  public List<GlimItem> paginateItems(final int givenPage) {
    final int page = givenPage - 1;

    int max = ((page * pageSize) + pageSize);
    if (max > pageItems.size()) max = pageItems.size();

    return IntStream.range(page * pageSize, max).mapToObj(pageItems::get).toList();
  }

  public int pagesCount() {
    return (int) Math.ceil((double) pageItems.size() / pageSize);
  }

  private void populatePage() {
    int slotIndex = 0;
    int filledSlotCount = 0;
    final int inventoryCapacity = getInventory().getSize();
    final List<GlimItem> itemsToDisplay = paginateItems(pageNum);
    int currentItemIndex = 0;

    while (filledSlotCount < pageSize && currentItemIndex < itemsToDisplay.size()) {
      if (slotIndex >= inventoryCapacity) {
        break;
      }
      if (item(slotIndex) != null || getInventory().getItem(slotIndex) != null) {
        slotIndex++;
        continue;
      }
      final GlimItem currentItem = itemsToDisplay.get(currentItemIndex);
      currentItemsBySlot.put(slotIndex, currentItem);
      getInventory().setItem(slotIndex, currentItem.itemStack());
      slotIndex++;
      filledSlotCount++;
      currentItemIndex++;
    }
  }

  public void clearPage() {
    currentItemsBySlot.forEach((key, value) -> getInventory().setItem(key, null));
    currentItemsBySlot.clear();
  }

  public void clearPageItems(final boolean update) {
    pageItems.clear();
    if (update) update();
  }

  public void clearPageItems() {
    clearPageItems(false);
  }

  public void updatePage() {
    clearPage();
    populatePage();
  }

  public int calculatePageSize() {
    int counter = 0;
    for (int slot = 0; slot < rows() * 9; slot++) {
      if (getInventory().getItem(slot) == null) {
        counter++;
      }
    }
    return counter;
  }

  public Map<Integer, GlimItem> itemsBySlot() {
    return unmodifiableMap(currentItemsBySlot);
  }

  public List<GlimItem> items() {
    return Collections.unmodifiableList(pageItems);
  }

  public int pageSize() {
    return pageSize;
  }

  public int pageNum() {
    return pageNum;
  }

  public void pageNum(final int pageNum) {
    this.pageNum = pageNum;
  }
}
