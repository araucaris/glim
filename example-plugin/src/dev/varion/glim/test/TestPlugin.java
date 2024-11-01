package dev.varion.glim.test;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import dev.varion.glim.Glim;
import dev.varion.glim.GlimItemBuilder;
import dev.varion.glim.gui.SimpleGui;
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
                final SimpleGui gui =
                    glim.creator()
                        .title(
                            miniMessage()
                                .deserialize("<red>example title</red>")
                                .decoration(TextDecoration.ITALIC, false))
                        .rows(5)
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
                                click.getWhoClicked().sendMessage(Component.text("hello"))));
                gui.open(event.getPlayer());
              }
            },
            this);
  }
}
