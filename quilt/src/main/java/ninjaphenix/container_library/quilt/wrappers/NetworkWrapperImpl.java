package ninjaphenix.container_library.quilt.wrappers;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import ninjaphenix.container_library.quilt.ScreenHandlerFactoryAdapter;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import ninjaphenix.container_library.wrappers.NetworkWrapper;
import org.quiltmc.loader.api.QuiltLoader;

public final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    protected void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType) {
        player.openMenu(new ScreenHandlerFactoryAdapter(title, inventory, factory, forcedScreenType));
    }

    @Override
    public boolean canOpenInventory(ServerPlayer player, BlockPos pos) {
        if (QuiltLoader.isModLoaded("flan")) {
            return ClaimHandler.getPermissionStorage(player.getLevel()).getForPermissionCheck(pos).canInteract(player, PermissionRegistry.OPENCONTAINER, pos, true);
        }
        return true;
    }
}
