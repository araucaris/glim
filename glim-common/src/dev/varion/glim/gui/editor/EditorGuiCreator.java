package dev.varion.glim.gui.editor;

import dev.varion.glim.gui.GuiCreator;

public final class EditorGuiCreator extends GuiCreator<EditorGui, EditorGuiCreator> {

  @Override
  public EditorGui create() {
    return new EditorGui(rows(), title(), modifiers());
  }
}
