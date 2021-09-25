package ninjaphenix.container_library.api.v2;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ninjaphenix.container_library.api.v2.client.NCL_ClientApiV2;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProviderV2 {
    /**
     * Return the openable block entity, {@link ninjaphenix.container_library.api.helpers.OpenableBlockEntities} can be used to supply more than one inventory.
     */
    default OpenableBlockEntityV2 getOpenableBlockEntity(Level world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OpenableBlockEntityV2 entity) {
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

    // Should be protected, only called from within class.
    default InteractionResult ncl_onBlockUse(Level world, BlockState state, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) {
            this.ncl_cOpenInventory(pos, hand, hit);
            return InteractionResult.SUCCESS;
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                this.ncl_sOpenInventory(world, state, pos, serverPlayer);
            }
            return InteractionResult.CONSUME;
        }
    }

    // Should be protected, only called from within class.
    default void ncl_cOpenInventory(BlockPos pos, InteractionHand hand, BlockHitResult hit) {
        NCL_ClientApiV2.openInventoryAt(pos, hand, hit);
    }

    // Should be protected, only called from within class.
    default void ncl_sOpenInventory(Level world, BlockState state, BlockPos pos, ServerPlayer player) {
        NetworkWrapper.getInstance().s_openInventory(player, this.getOpenableBlockEntity(world, state, pos), this::onInitialOpen);
    }
}
