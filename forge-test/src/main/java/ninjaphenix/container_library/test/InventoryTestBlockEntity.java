package ninjaphenix.container_library.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;

public class InventoryTestBlockEntity extends RandomizableContainerBlockEntity implements OpenableBlockEntityV2 {
    private final int inventorySize;
    private NonNullList<ItemStack> inventory;

    public InventoryTestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventorySize = ((InventoryTestBlock) state.getBlock()).getInventorySize();
        this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) {
        inventory = stacks;
    }

    @Override
    protected BaseComponent getDefaultName() {
        return new TextComponent("Inventory " + inventorySize);
    }

    // Not used.
    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return inventorySize;
    }
}
