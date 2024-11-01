# Glim *(Błysk)*

Glim is a powerful and flexible GUI framework designed for plugin development. 
It simplifies the creation of intricate, paginated, and interactive guis within the game, 
leveraging the Bukkit API. 
Here’s a quick guide to get started.

## Getting Started

### Add Repository

```kotlin
maven("https://repo.varion.dev/snapshots")
```

### Add Dependencies

**Core:**

```kotlin
implementation("dev.varion.glim:glim-common:1.0.1-SNAPSHOT")
```

### Example Usage

**Simple gui**
```java
final Glim glim = Glim.create(this); //JavaPlugin instance
final SimpleGui gui = glim.creator()
    .title(miniMessage().deserialize("<red>example title</red>")
        .decoration(TextDecoration.ITALIC, false))
    .rows(5)
    .disableAllInteractions()
    .create();
    gui.filler()
      .fillBorder(GlimItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGlim());
    gui.set(5, 3, GlimItemBuilder.from(Material.STONE_BUTTON)
      .asGlim(click -> click.getWhoClicked().sendMessage(Component.text("hello"))));
    gui.open(event.getPlayer());
```

**Paginated gui**
```java
final Glim glim = Glim.create(this); //JavaPlugin instance
final PaginatedGui gui = glim.paginatedCreator()
    .title(miniMessage().deserialize("<red>example title</red>")
        .decoration(TextDecoration.ITALIC, false))
    .rows(5)
    .pageSize(21)
    .disableAllInteractions()
    .create();

    gui.filler()
      .fillBorder(GlimItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGlim());

    gui.set(5, 3, GlimItemBuilder.from(Material.STONE_BUTTON)
      .asGlim(click -> click.getWhoClicked().sendMessage(Component.text(gui.previous()))));

    gui.set(5, 7, GlimItemBuilder.from(Material.STONE_BUTTON)
      .asGlim(click -> click.getWhoClicked().sendMessage(Component.text(gui.next()))));

    for (int i = 0; i < 320; i++) {
        final int itemIndex = i;
        gui.insert(GlimItemBuilder.from(Material.COBBLESTONE)
            .asGlim(click -> click.getWhoClicked().sendMessage("hello from " + itemIndex)));
    }

    gui.open(event.getPlayer());
```

---

<p align="center">
  <img height="100em" src="https://count.getloli.com/get/@:glim?theme=rule33" alt="loli"/>
</p>