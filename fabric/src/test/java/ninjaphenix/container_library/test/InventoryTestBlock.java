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
import net.minecraft.world.World;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.api.client.NCL_ClientApi;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlock extends Block implements BlockEntityProvider, OpenableBlockEntityProvider {
    private final int inventorySize;

    public InventoryTestBlock(AbstractBlock.Settings properties, int inventorySize) {
        super(properties);
        this.inventorySize = inventorySize;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            NCL_ClientApi.openInventoryAt(pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onInitialOpen(ServerPlayerEntity player) {
        player.sendSystemMessage(new LiteralText(player.getName().getString() + " has opened a test inventory with " + inventorySize + " slots!"), Util.NIL_UUID);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state);
    }

    public int getInventorySize() {
        return inventorySize;
    }
}
