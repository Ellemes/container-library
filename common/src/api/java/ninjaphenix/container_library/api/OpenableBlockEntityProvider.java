package ninjaphenix.container_library.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProvider {
    /**
     * Return a list of inventories which make up the inventory to be open, currently only supports upto 2 inventories.
     */
    default List<OpenableBlockEntity> getParts(Level world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OpenableBlockEntity entity) {
            return List.of(entity);
        }
        return List.of();
    }

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     */
    default void onInitialOpen(ServerPlayer player) {

    }
}
