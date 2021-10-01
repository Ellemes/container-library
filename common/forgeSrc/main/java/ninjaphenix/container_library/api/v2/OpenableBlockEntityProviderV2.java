package ninjaphenix.container_library.api.v2;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import ninjaphenix.container_library.api.v2.client.NCL_ClientApiV2;
import ninjaphenix.container_library.api.v2.helpers.OpenableBlockEntitiesV2;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProviderV2 {
    /**
     * Return the openable block entity, {@link OpenableBlockEntitiesV2} can be used to supply more than one inventory.
     */
    default OpenableBlockEntityV2 getOpenableBlockEntity(World world, BlockState state, BlockPos pos) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof OpenableBlockEntityV2) {
            return (OpenableBlockEntityV2) entity;
        }
        return null;
    }

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     * Note: more context can be provided if needed, namely ServerWorld, BlockState and BlockPos.
     */
    default void onInitialOpen(ServerPlayerEntity player) {

    }

    /**
     * Note: when this returns {@link ActionResultType#FAIL} a packet will not be sent to the server and the player's hand will swing.
     * <p/>
     * Intended to be protected.
     */
    default ActionResultType ncl_onBlockUse(World world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide()) {
            return this.ncl_cOpenInventory(pos, hand, hit) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else {
            if (player instanceof ServerPlayerEntity) {
                this.ncl_sOpenInventory(world, state, pos, (ServerPlayerEntity) player);
            }
            return ActionResultType.CONSUME;
        }
    }

    /**
     * When this method returns {@code false} {@link Block#use} should return {@link ActionResultType#FAIL}
     * <p/>
     * Intended to be protected.
     */
    default boolean ncl_cOpenInventory(BlockPos pos, Hand hand, BlockRayTraceResult hit) {
        return NCL_ClientApiV2.openInventoryAt(pos, hand, hit);
    }

    /**
     * Intended to be protected.
     */
    default void ncl_sOpenInventory(World world, BlockState state, BlockPos pos, ServerPlayerEntity player) {
        NetworkWrapper.getInstance().s_openInventory(player, this.getOpenableBlockEntity(world, state, pos), this::onInitialOpen);
    }
}
