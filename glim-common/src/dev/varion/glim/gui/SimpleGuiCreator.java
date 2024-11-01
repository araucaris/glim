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
    final SimpleGui gui;
    if (guiType == null || guiType == GuiType.CHEST) {
      gui = new SimpleGui(rows(), title(), modifiers());
    } else {
      gui = new SimpleGui(guiType, title(), modifiers());
    }
    return gui;
  }
}
