package ninjaphenix.container_library.api;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @deprecated Use V2 instead {@link ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2 }
 */
public interface OpenableBlockEntityProvider {
    /**
     * Return the openable block entity, {@link ninjaphenix.container_library.api.helpers.OpenableBlockEntities} can be used to supply more than one inventory.
     */
    default OpenableBlockEntity getOpenableBlockEntity(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OpenableBlockEntity entity) {
            return entity;
        }
        return null;
    }

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     * Note: more context can be provided if needed, namely ServerWorld, BlockState and BlockPos.
     */
    default void onInitialOpen(ServerPlayerEntity player) {

    }
}
