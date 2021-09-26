package ninjaphenix.container_library.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;

public interface ClientScreenHandlerFactory<T extends Container> {
    T create(int syncId, PlayerInventory playerInventory, PacketBuffer buffer);
}
