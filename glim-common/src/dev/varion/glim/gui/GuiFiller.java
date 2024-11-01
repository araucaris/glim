package dev.varion.glim.gui;

import static dev.varion.glim.Glim.getSlotFromRowCol;

import dev.varion.glim.GlimItem;
import dev.varion.glim.gui.paginated.PaginatedGui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GuiFiller {

  private final Gui gui;

  public GuiFiller(final Gui gui) {
    this.gui = gui;
  }

  public void fillTop(final GlimItem item) {
    fillTop(Collections.singletonList(item));
  }

  public void fillTop(final List<GlimItem> guiItems) {
    final List<GlimItem> items = repeatList(guiItems);
    for (int i = 0; i < 9; i++) {
      if (!gui.contains(i)) gui.set(i, items.get(i));
    }
  }

  public void fillBottom(final GlimItem item) {
    fillBottom(Collections.singletonList(item));
  }

  public void fillBottom(final List<GlimItem> guiItems) {
    final int rows = gui.rows();
    final List<GlimItem> items = repeatList(guiItems);
    for (int i = 9; i > 0; i--) {
      if (gui.guiItem((rows * 9) - i) == null) {
        gui.set((rows * 9) - i, items.get(i));
      }
    }
  }

  public void fillBorder(final GlimItem item) {
    fillBorder(Collections.singletonList(item));
  }

  public void fillBorder(final List<GlimItem> guiItems) {
    final int rows = gui.rows();
    if (rows <= 2) return;

    final List<GlimItem> items = repeatList(guiItems);

    for (int i = 0; i < rows * 9; i++) {
      if ((i <= 8) || (i >= (rows * 9) - 8) && (i <= (rows * 9) - 2) || i % 9 == 0 || i % 9 == 8)
        gui.set(i, items.get(i));
    }
  }

  public void fill(final GlimItem item) {
    fill(Collections.singletonList(item));
  }

  public void fill(final List<GlimItem> guiItems) {
    if (gui instanceof PaginatedGui) {
      throw new dev.varion.glim.GlimException(
          "Full filling a GUI is not supported in a Paginated GUI!");
    }

    final GuiType type = gui.guiType();

    final int fill;
    if (type == GuiType.CHEST) {
      fill = gui.rows() * type.limit();
    } else {
      fill = type.limit();
    }

    final List<GlimItem> items = repeatList(guiItems);
    for (int i = 0; i < fill; i++) {
      if (gui.guiItem(i) == null) gui.set(i, items.get(i));
    }
  }

  public void fillSide(final Side side, final List<GlimItem> items) {
    switch (side) {
      case LEFT -> fillBetweenPoints(1, 1, gui.rows(), 1, items);
      case RIGHT -> fillBetweenPoints(1, 9, gui.rows(), 9, items);
      case BOTH -> {
        fillBetweenPoints(1, 1, gui.rows(), 1, items);
        fillBetweenPoints(1, 9, gui.rows(), 9, items);
      }
    }
  }

  private List<GlimItem> repeatList(final List<GlimItem> items) {
    final List<GlimItem> repeated = new ArrayList<>();
    Collections.nCopies(gui.rows() * 9, items).forEach(repeated::addAll);
    return repeated;
  }

  public void fillBetweenPoints(
      final int rowFrom, final int colFrom, final int rowTo, final int colTo, final GlimItem item) {
    fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(item));
  }

  public void fillBetweenPoints(
      final int rowFrom,
      final int colFrom,
      final int rowTo,
      final int colTo,
      final List<GlimItem> guiItems) {
    final int minRow = Math.min(rowFrom, rowTo);
    final int maxRow = Math.max(rowFrom, rowTo);
    final int minCol = Math.min(colFrom, colTo);
    final int maxCol = Math.max(colFrom, colTo);

    final int rows = gui.rows();
    final List<GlimItem> items = repeatList(guiItems);

    for (int row = 1; row <= rows; row++) {
      for (int col = 1; col <= 9; col++) {
        final int slot = getSlotFromRowCol(row, col);
        if (!((row >= minRow && row <= maxRow) && (col >= minCol && col <= maxCol))) continue;

        gui.set(slot, items.get(slot));
      }
    }
  }

  public enum Side {
    LEFT,
    RIGHT,
    BOTH
  }
}
