package ninjaphenix.container_library.wrappers;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import org.jetbrains.annotations.Nullable;

public final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    protected void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
                buffer.writeInt(inventory.getContainerSize());
                if (forcedScreenType != null) {
                    buffer.writeResourceLocation(forcedScreenType);
                }
            }

            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        });
    }

    @Override
    public boolean canOpenInventory(ServerPlayer player, BlockPos pos) {
        if (FabricLoader.getInstance().isModLoaded("flan")) {
            return ClaimHandler.getPermissionStorage(player.getLevel()).getForPermissionCheck(pos).canInteract(player, PermissionRegistry.OPENCONTAINER, pos, true);
        }
        return true;
    }
}
