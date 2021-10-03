package ninjaphenix.container_library.test;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlock extends Block implements EntityBlock, OpenableBlockEntityProviderV2 {
    private final int inventorySize;

    public InventoryTestBlock(BlockBehaviour.Properties settings, int inventorySize) {
        super(settings);
        this.inventorySize = inventorySize;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return this.ncl_onBlockUse(world, state, pos, player, hand, hit);
    }

    @Override
    public void onInitialOpen(ServerPlayer player) {
        player.sendMessage(new TextComponent(player.getName().getString() + " has opened a test inventory with " + inventorySize + " slots!"), Util.NIL_UUID);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state);
    }

    public int getInventorySize() {
        return inventorySize;
    }
}
