package ellemes.container_library.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ClientScreenHandlerFactory<T extends AbstractContainerMenu> {
    T create(int syncId, Inventory playerInventory, FriendlyByteBuf buffer);
}
