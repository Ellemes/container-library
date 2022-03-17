package ninjaphenix.container_library.wrappers;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    public boolean canOpenInventory(ServerPlayer player, BlockPos pos) {
        if (FabricLoader.getInstance().isModLoaded("flan")) {
            return ClaimHandler.getPermissionStorage(player.getLevel()).getForPermissionCheck(pos).canInteract(player, PermissionRegistry.OPENCONTAINER, pos, true);
        }
        return true;
    }
}
