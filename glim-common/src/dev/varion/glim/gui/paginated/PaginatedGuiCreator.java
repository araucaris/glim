package dev.varion.glim.gui.paginated;

import dev.varion.glim.gui.GuiCreator;
import java.util.function.Consumer;

public final class PaginatedGuiCreator extends GuiCreator<PaginatedGui, PaginatedGuiCreator> {

  private int pageSize;

  public PaginatedGuiCreator pageSize(final int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  @Override
  public PaginatedGui create() {
    final PaginatedGui gui = new PaginatedGui(rows(), pageSize, title(), modifiers());

    final Consumer<PaginatedGui> consumer = consumer();
    if (consumer != null) consumer.accept(gui);

    return gui;
  }
}
