package dev.varion.glim.gui.paginated;

import static dev.varion.glim.Glim.getSlotFromRowCol;
import static java.util.Collections.unmodifiableMap;

import dev.varion.glim.GlimItem;
import dev.varion.glim.gui.Gui;
import dev.varion.glim.modifier.InteractionModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public final class PaginatedGui extends Gui {

  private final List<GlimItem> pageItems = new ArrayList<>();
  private final Map<Integer, GlimItem> currentPage;

  private int pageSize;
  private int pageNum = 1;

  public PaginatedGui(
      final int rows,
      final int pageSize,
      final Component title,
      final Set<InteractionModifier> interactionModifiers) {
    super(rows, title, interactionModifiers);
    this.pageSize = pageSize;
    final int inventorySize = rows * 9;
    currentPage = new LinkedHashMap<>(inventorySize);
  }

  public Gui pageSize(final int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public void insert(final GlimItem item) {
    pageItems.add(item);
  }

  @Override
  public void insert(final GlimItem... items) {
    pageItems.addAll(Arrays.asList(items));
  }

  @Override
  public void update() {
    getInventory().clear();
    populateGui();

    updatePage();
  }

  public void updatePageItem(final int slot, final ItemStack itemStack) {
    if (!currentPage.containsKey(slot)) return;
    final GlimItem item = currentPage.get(slot);
    item.itemStack(itemStack);
    getInventory().setItem(slot, item.itemStack());
  }

  public void updatePageItem(final int row, final int col, final ItemStack itemStack) {
    updateItem(getSlotFromRowCol(row, col), itemStack);
  }

  public void updatePageItem(final int slot, final GlimItem item) {
    if (!currentPage.containsKey(slot)) return;

    final GlimItem oldItem = currentPage.get(slot);
    final int index = pageItems.indexOf(currentPage.get(slot));

    currentPage.put(slot, item);
    pageItems.set(index, item);
    getInventory().setItem(slot, item.itemStack());
  }

  public void updatePageItem(final int row, final int col, final GlimItem item) {
    updateItem(getSlotFromRowCol(row, col), item);
  }

  public void removePageItem(final GlimItem item) {
    pageItems.remove(item);
    updatePage();
  }

  public void removePageItem(final ItemStack item) {
    final Optional<GlimItem> guiItem =
        pageItems.stream().filter(it -> it.itemStack().equals(item)).findFirst();
    guiItem.ifPresent(this::removePageItem);
  }

  @Override
  public void open(final HumanEntity player) {
    open(player, 1);
  }

  public void open(final HumanEntity player, final int openPage) {
    if (player.isSleeping()) return;
    if (openPage <= getPagesNum() || openPage > 0) pageNum = openPage;

    getInventory().clear();
    currentPage.clear();

    populateGui();

    if (pageSize == 0) pageSize = calculatePageSize();

    populatePage();

    player.openInventory(getInventory());
  }

  public boolean next() {
    if (pageNum + 1 > getPagesNum()) return false;

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

  public int getNextPageNum() {
    if (pageNum + 1 > getPagesNum()) return pageNum;
    return pageNum + 1;
  }

  public int getPrevPageNum() {
    if (pageNum - 1 == 0) return pageNum;
    return pageNum - 1;
  }

  public GlimItem pageItem(final int slot) {
    return currentPage.get(slot);
  }

  public List<GlimItem> paginateItems(final int givenPage) {
    final int page = givenPage - 1;

    int max = ((page * pageSize) + pageSize);
    if (max > pageItems.size()) max = pageItems.size();

    return IntStream.range(page * pageSize, max)
        .mapToObj(pageItems::get)
        .collect(Collectors.toList());
  }

  public int getPagesNum() {
    return (int) Math.ceil((double) pageItems.size() / pageSize);
  }

  private void populatePage() {
    int slot = 0;
    final int inventorySize = getInventory().getSize();
    for (final GlimItem glimItem : paginateItems(pageNum)) {
      if (slot >= inventorySize) {
        break;
      }

      if (guiItem(slot) != null || getInventory().getItem(slot) != null) {
        slot++;
        continue;
      }

      currentPage.put(slot, glimItem);
      getInventory().setItem(slot, glimItem.itemStack());
      slot++;
    }
  }

  public void clearPage() {
    for (final Map.Entry<Integer, GlimItem> entry : currentPage.entrySet()) {
      getInventory().setItem(entry.getKey(), null);
    }
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

  public Map<Integer, GlimItem> getCurrentPageItems() {
    return unmodifiableMap(currentPage);
  }

  public List<GlimItem> getPageItems() {
    return Collections.unmodifiableList(pageItems);
  }

  public int getCurrentPageNum() {
    return pageNum;
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

  public Map<Integer, GlimItem> currentPageItems() {
    return unmodifiableMap(currentPage);
  }
}
