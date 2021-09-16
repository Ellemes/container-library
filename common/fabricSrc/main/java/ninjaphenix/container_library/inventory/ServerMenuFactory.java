package ninjaphenix.container_library.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

public interface ServerMenuFactory {
    ScreenHandler create(int windowId, Inventory container, PlayerInventory playerInventory);
}
