package dev.varion.glim.gui.editor;

import dev.varion.glim.gui.Gui;
import dev.varion.glim.modifier.InteractionModifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public final class EditorGui extends Gui {

  public EditorGui(
      final int rows, final Component title, final Set<InteractionModifier> interactionModifiers) {
    super(rows, title, interactionModifiers);
  }

  public Map<Integer, ItemStack> addItem(final ItemStack... items) {
    return Collections.unmodifiableMap(getInventory().addItem(items));
  }

  public Map<Integer, ItemStack> addItem(final List<ItemStack> items) {
    return addItem(items.toArray(new ItemStack[0]));
  }

  @Override
  public void open(final HumanEntity player) {
    if (player.isSleeping()) {
      return;
    }

    populateGui();
    player.openInventory(getInventory());
  }
}
