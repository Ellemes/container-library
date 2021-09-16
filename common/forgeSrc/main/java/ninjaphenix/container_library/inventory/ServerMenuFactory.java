package ninjaphenix.container_library.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ServerMenuFactory {
    AbstractContainerMenu create(int windowId, Container container, Inventory playerInventory);
}
