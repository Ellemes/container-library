package ninjaphenix.container_library.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ClientMenuFactory<T extends AbstractContainerMenu> {
    T create(int windowId, Inventory inventory, FriendlyByteBuf buffer);
}
