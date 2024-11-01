package dev.varion.glim.gui.editor;

import dev.varion.glim.gui.GuiCreator;
import java.util.function.Consumer;

public final class EditorGuiCreator extends GuiCreator<EditorGui, EditorGuiCreator> {

  @Override
  public EditorGui create() {
    final EditorGui gui = new EditorGui(rows(), title(), modifiers());

    final Consumer<EditorGui> consumer = consumer();
    if (consumer != null) consumer.accept(gui);

    return gui;
  }
}
