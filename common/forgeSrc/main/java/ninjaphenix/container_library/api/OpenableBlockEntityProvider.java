package ninjaphenix.container_library.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @deprecated Use V2 instead {@link ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2 }
 */
@Deprecated
public interface OpenableBlockEntityProvider {
    /**
     * Return the openable block entity, {@link ninjaphenix.container_library.api.helpers.OpenableBlockEntities} can be used to supply more than one inventory.
     */
    default OpenableBlockEntity getOpenableBlockEntity(Level world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OpenableBlockEntity entity) {
            return entity;
        }
        return null;
    }

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     * Note: more context can be provided if needed, namely ServerWorld, BlockState and BlockPos.
     */
    default void onInitialOpen(ServerPlayer player) {

    }
}
