package dev.varion.glim;

import static org.bukkit.Bukkit.getPluginManager;

import dev.varion.glim.gui.GuiController;
import dev.varion.glim.gui.GuiType;
import dev.varion.glim.gui.SimpleGuiCreator;
import dev.varion.glim.gui.editor.EditorGuiCreator;
import dev.varion.glim.gui.paginated.PaginatedGuiCreator;
import dev.varion.glim.modifier.InteractionModifierController;
import org.bukkit.plugin.java.JavaPlugin;

public final class Glim {

  private Glim() {}

  public static Glim create(final JavaPlugin plugin) {
    getPluginManager().registerEvents(new GuiController(), plugin);
    getPluginManager().registerEvents(new InteractionModifierController(), plugin);
    return new Glim();
  }

  public SimpleGuiCreator creator(final GuiType type) {
    return new SimpleGuiCreator(type);
  }

  public SimpleGuiCreator creator() {
    return creator(GuiType.CHEST);
  }

  public EditorGuiCreator editorCreator() {
    return new EditorGuiCreator();
  }

  public PaginatedGuiCreator paginatedCreator() {
    return new PaginatedGuiCreator();
  }
}
