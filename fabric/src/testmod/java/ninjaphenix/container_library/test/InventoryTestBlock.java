package ninjaphenix.container_library.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlock extends Block implements BlockEntityProvider, OpenableBlockEntityProviderV2 {
    private final int inventorySize;

    public InventoryTestBlock(AbstractBlock.Settings settings, int inventorySize) {
        super(settings);
        this.inventorySize = inventorySize;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.ncl_onBlockUse(world, state, pos, player, hand, hit);
    }

    @Override
    public void onInitialOpen(ServerPlayerEntity player) {
        player.sendSystemMessage(new LiteralText(player.getName().getString() + " has opened a test inventory with " + inventorySize + " slots!"), Util.NIL_UUID);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new InventoryTestBlockEntity(Main.getBlockEntityType(), this.getInventorySize());
    }

    public int getInventorySize() {
        return inventorySize;
    }
}
