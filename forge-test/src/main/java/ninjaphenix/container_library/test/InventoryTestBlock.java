package ninjaphenix.container_library.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlock extends Block implements OpenableBlockEntityProviderV2 {
    private final int inventorySize;

    public InventoryTestBlock(AbstractBlock.Properties settings, int inventorySize) {
        super(settings);
        this.inventorySize = inventorySize;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return this.ncl_onBlockUse(world, state, pos, player, hand, hit);
    }

    @Override
    public void onInitialOpen(ServerPlayerEntity player) {
        player.sendMessage(new StringTextComponent(player.getName().getString() + " has opened a test inventory with " + inventorySize + " slots!"), Util.NIL_UUID);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InventoryTestBlockEntity(Main.getBlockEntityType(), this.getInventorySize());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public int getInventorySize() {
        return inventorySize;
    }
}
