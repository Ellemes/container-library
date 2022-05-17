package ellemes.container_library.forge.wrappers;

import ellemes.container_library.inventory.ServerScreenHandlerFactory;
import ellemes.container_library.wrappers.NetworkWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    protected void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        }, buffer -> {
            buffer.writeInt(inventory.getContainerSize());
            if (forcedScreenType != null) {
                buffer.writeResourceLocation(forcedScreenType);
            }
        });
    }

    @Override
    public boolean canOpenInventory(ServerPlayer player, BlockPos pos) {
        return true;
    }
}
