package ellemes.container_library.api.v2;

import ellemes.container_library.CommonMain;
import ellemes.container_library.api.v2.client.NCL_ClientApiV2;
import ellemes.container_library.api.v2.helpers.OpenableBlockEntitiesV2;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProviderV2 {
    /**
     * Return the openable block entity, {@link OpenableBlockEntitiesV2} can be used to supply more than one inventory.
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

    /**
     * Note: when this returns {@link InteractionResult#FAIL} a packet will not be sent to the server and the player's hand will swing.
     * <p/>
     * Intended to be protected.
     */
    default InteractionResult ncl_onBlockUse(Level world, BlockState state, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) {
            return this.ncl_cOpenInventory(pos, hand, hit) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                this.ncl_sOpenInventory(world, state, pos, serverPlayer);
            }
            return InteractionResult.CONSUME;
        }
    }

    /**
     * When this method returns {@code false} {@link Block#use} should return {@link InteractionResult#FAIL}
     * <p/>
     * Intended to be protected.
     */
    default boolean ncl_cOpenInventory(BlockPos pos, InteractionHand hand, BlockHitResult hit) {
        return NCL_ClientApiV2.openInventoryAt(pos, hand, hit, false);
    }

    /**
     * When this method returns {@code false} {@link Block#use} should return {@link InteractionResult#FAIL}
     * <p/>
     * This does the same as {@link OpenableBlockEntityProviderV2#ncl_cOpenInventory(BlockPos, InteractionHand, BlockHitResult)} except it skips the screen option check.
     * <p>
     * Intended to be protected.
     */
    default boolean ncl_cOpenInventoryNoScreenCheck(BlockPos pos, InteractionHand hand, BlockHitResult hit) {
        return NCL_ClientApiV2.openInventoryAt(pos, hand, hit, true);
    }

    /**
     * Intended to be protected.
     */
    default void ncl_sOpenInventory(Level world, BlockState state, BlockPos pos, ServerPlayer player) {
        this.ncl_sOpenInventory(world, state, pos, player, null);
    }

    /**
     * Intended to be protected.
     */
    default void ncl_sOpenInventory(Level world, BlockState state, BlockPos pos, ServerPlayer player, ResourceLocation forcedScreenType) {
        CommonMain.getNetworkWrapper().s_openInventory(player, this.getOpenableBlockEntity(world, state, pos), this::onInitialOpen, pos, forcedScreenType);
    }
}
