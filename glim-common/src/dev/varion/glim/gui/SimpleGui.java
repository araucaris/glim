package dev.varion.glim.gui;

import dev.varion.glim.modifier.InteractionModifier;
import java.util.Set;
import net.kyori.adventure.text.Component;

public final class SimpleGui extends Gui {

  public SimpleGui(
      final int rows, final Component title, final Set<InteractionModifier> interactionModifiers) {
    super(rows, title, interactionModifiers);
  }

  public SimpleGui(
      final GuiType guiType,
      final Component title,
      final Set<InteractionModifier> interactionModifiers) {
    super(guiType, title, interactionModifiers);
  }
}
