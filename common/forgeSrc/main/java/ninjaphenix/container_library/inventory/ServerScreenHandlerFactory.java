package ninjaphenix.container_library.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;

public interface ServerScreenHandlerFactory {
    Container create(int syncId, IInventory inventory, PlayerInventory playerInventory);
}
