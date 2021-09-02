package ninjaphenix.container_library.internal.api.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface ServerMenuFactory {
    AbstractContainerMenu create(int windowId, Container container, Inventory playerInventory);
}
