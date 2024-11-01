package dev.varion.glim.gui.paginated;

import dev.varion.glim.gui.GuiCreator;

public final class PaginatedGuiCreator extends GuiCreator<PaginatedGui, PaginatedGuiCreator> {

  private int pageSize;

  public PaginatedGuiCreator pageSize(final int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  @Override
  public PaginatedGui create() {
    return new PaginatedGui(rows(), pageSize, title(), modifiers());
  }
}
