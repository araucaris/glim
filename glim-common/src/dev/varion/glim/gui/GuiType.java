package dev.varion.glim.gui;

import org.bukkit.event.inventory.InventoryType;

public enum GuiType {
  CHEST(InventoryType.CHEST, 9),
  WORKBENCH(InventoryType.WORKBENCH, 9),
  HOPPER(InventoryType.HOPPER, 5),
  DISPENSER(InventoryType.DISPENSER, 8),
  BREWING(InventoryType.BREWING, 4);

  private final InventoryType type;
  private final int limit;

  GuiType(final InventoryType type, final int limit) {
    this.type = type;
    this.limit = limit;
  }

  public InventoryType inventoryType() {
    return type;
  }

  public int limit() {
    return limit;
  }
}
