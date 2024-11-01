package dev.varion.glim;

import dev.varion.glim.gui.SimpleGui;
import dev.varion.glim.gui.paginated.PaginatedGui;
import dev.varion.glim.modifier.InteractionModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class GlimTest {

  private GlimTest() {}

  public static void main(final String[] args) {
    final Glim glim = Glim.create(null);
    final SimpleGui gui =
        glim.creator()
            .title(Component.text("test"))
            .rows(5)
            .enableAllInteractions()
            .enable(InteractionModifier.ITEM_DROP)
            .create();
    gui.insert(
        GlimItemBuilder.of(Material.ACACIA_BUTTON)
            .name(Component.text("button"))
            .asGlim(event -> event.getWhoClicked().sendMessage(Component.text("click"))));
    gui.open(null);

    final PaginatedGui paginatedGui =
        glim.paginatedCreator()
            .title(Component.text("test"))
            .rows(5)
            .pageSize(28)
            .enableAllInteractions()
            .enable(InteractionModifier.ITEM_DROP)
            .create();
    paginatedGui.next();
    paginatedGui.previous();
    paginatedGui.insert(
        new GlimItem(new ItemStack(Material.ACACIA_BUTTON)),
        new GlimItem(new ItemStack(Material.ACACIA_BUTTON)),
        new GlimItem(new ItemStack(Material.ACACIA_BUTTON)),
        new GlimItem(new ItemStack(Material.ACACIA_BUTTON)),
        new GlimItem(new ItemStack(Material.ACACIA_BUTTON)));
    paginatedGui.open(null, 3);
  }
}
