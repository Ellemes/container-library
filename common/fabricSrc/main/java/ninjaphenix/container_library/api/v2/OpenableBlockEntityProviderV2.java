package ninjaphenix.container_library.api.v2;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.container_library.api.v2.client.NCL_ClientApiV2;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProviderV2 {
    /**
     * Return the openable block entity, {@link ninjaphenix.container_library.api.v2.helpers.OpenableBlockEntitiesV2} can be used to supply more than one inventory.
     */
    default OpenableBlockEntityV2 getOpenableBlockEntity(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OpenableBlockEntityV2 entity) {
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

    // Should be protected, only called from within class.
    default ActionResult ncl_onBlockUse(World world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            this.ncl_cOpenInventory(pos, hand, hit);
            return ActionResult.SUCCESS;
        } else {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                this.ncl_sOpenInventory(world, state, pos, serverPlayer);
            }
            return ActionResult.CONSUME;
        }
    }

    // Should be protected, only called from within class.
    default void ncl_cOpenInventory(BlockPos pos, Hand hand, BlockHitResult hit) {
        NCL_ClientApiV2.openInventoryAt(pos, hand, hit);
    }

    // Should be protected, only called from within class.
    default void ncl_sOpenInventory(World world, BlockState state, BlockPos pos, ServerPlayerEntity player) {
        NetworkWrapper.getInstance().s_openInventory(player, this.getOpenableBlockEntity(world, state, pos), this::onInitialOpen);
    }
}
