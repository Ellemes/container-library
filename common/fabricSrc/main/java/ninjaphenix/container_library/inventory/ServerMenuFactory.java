package ninjaphenix.container_library.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

public interface ServerMenuFactory {
    ScreenHandler create(int syncId, Inventory inventory, PlayerInventory playerInventory);
}
