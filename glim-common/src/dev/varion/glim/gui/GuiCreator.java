package dev.varion.glim.gui;

import dev.varion.glim.modifier.InteractionModifier;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;

@SuppressWarnings("unchecked")
public abstract class GuiCreator<G extends Gui, B extends GuiCreator<G, B>> {

  private final EnumSet<InteractionModifier> interactionModifiers;
  private Component title;
  private int rows;

  protected GuiCreator() {
    interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    title = Component.empty();
    rows = 1;
  }

  public B rows(final int rows) {
    this.rows = rows;
    return (B) this;
  }

  public B title(final Component title) {
    this.title = title;
    return (B) this;
  }

  public B disable(final InteractionModifier modifier) {
    interactionModifiers.remove(modifier);
    return (B) this;
  }

  public B disableAllInteractions() {
    interactionModifiers.clear();
    return (B) this;
  }

  public B enable(final InteractionModifier modifier) {
    interactionModifiers.add(modifier);
    return (B) this;
  }

  public B enableAllInteractions() {
    interactionModifiers.addAll(List.of(InteractionModifier.values()));
    return (B) this;
  }

  public abstract G create();

  protected Component title() {
    return title;
  }

  protected int rows() {
    return rows;
  }

  protected Set<InteractionModifier> modifiers() {
    return interactionModifiers;
  }
}
