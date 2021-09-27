package ninjaphenix.container_library.wrappers;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import org.jetbrains.annotations.Nullable;

final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    protected void openScreenHandler(ServerPlayerEntity player, Inventory inventory, ServerScreenHandlerFactory factory, Text title) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buffer) {
                buffer.writeInt(inventory.size());
            }

            @Override
            public Text getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        });
    }
}
