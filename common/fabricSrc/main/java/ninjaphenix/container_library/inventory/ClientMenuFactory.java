package ninjaphenix.container_library.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

public interface ClientMenuFactory<T extends ScreenHandler> {
    T create(int windowId, PlayerInventory playerInventory, PacketByteBuf buffer);
}
