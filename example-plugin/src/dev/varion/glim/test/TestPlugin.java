package dev.varion.glim.test;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import dev.varion.glim.Glim;
import dev.varion.glim.GlimItemBuilder;
import dev.varion.glim.gui.paginated.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final Glim glim = Glim.create(this);
    getServer()
        .getPluginManager()
        .registerEvents(
            new Listener() {
              @EventHandler
              public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
                final PaginatedGui gui =
                    glim.paginatedCreator()
                        .title(
                            miniMessage()
                                .deserialize("<red>example title</red>")
                                .decoration(TextDecoration.ITALIC, false))
                        .rows(5)
                        .pageSize(21)
                        .disableAllInteractions()
                        .create();
                gui.filler()
                    .fillBorder(GlimItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGlim());
                gui.set(
                    5,
                    3,
                    GlimItemBuilder.from(Material.STONE_BUTTON)
                        .asGlim(
                            click ->
                                click.getWhoClicked().sendMessage(Component.text(gui.previous()))));
                gui.set(
                    5,
                    7,
                    GlimItemBuilder.from(Material.STONE_BUTTON)
                        .asGlim(
                            click ->
                                click.getWhoClicked().sendMessage(Component.text(gui.next()))));
                for (int i = 0; i < 320; i++) {
                  final int itemIndex = i;
                  gui.insert(
                      GlimItemBuilder.from(Material.COBBLESTONE)
                          .asGlim(
                              click ->
                                  click.getWhoClicked().sendMessage("hello from " + itemIndex)));
                }
                gui.open(event.getPlayer());
              }
            },
            this);
  }
}
