package dev.varion.glim.gui;

public final class SimpleGuiCreator extends GuiCreator<SimpleGui, SimpleGuiCreator> {

  private GuiType guiType;

  public SimpleGuiCreator(final GuiType guiType) {
    this.guiType = guiType;
  }

  public SimpleGuiCreator type(final GuiType guiType) {
    this.guiType = guiType;
    return this;
  }

  @Override
  public SimpleGui create() {
    return guiType == null || guiType == GuiType.CHEST
        ? new SimpleGui(rows(), title(), modifiers())
        : new SimpleGui(guiType, title(), modifiers());
  }
}
