package ellemes.container_library.api.v3;

import ellemes.container_library.api.v3.context.Context;
import ellemes.container_library.api.v3.helpers.OpenableInventories;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Can be implemented on blocks, items, or entities.
 */
public interface OpenableInventoryProvider<T extends Context> {
    /**
     * Return the openable inventory, {@link OpenableInventories} can be used to supply more than one inventory.
     */
    OpenableInventory getOpenableInventory(T context);

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     * Note: more context can be provided if needed, namely ServerWorld.
     */
    default void onInitialOpen(ServerPlayer player) {

    }

    default void openBlockInventory(Level world, BlockPos pos) {

    }

    default void openEntityInventory(Level world, Entity entity) {

    }

    default void openItemInventory(Level world, int slotId) {

    }
}
