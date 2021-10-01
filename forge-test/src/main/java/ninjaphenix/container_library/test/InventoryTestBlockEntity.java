package ninjaphenix.container_library.test;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;

public class InventoryTestBlockEntity extends LockableLootTileEntity implements OpenableBlockEntityV2 {
    private int inventorySize;
    private NonNullList<ItemStack> inventory;

    public InventoryTestBlockEntity(TileEntityType<?> type, int inventorySize) {
        super(type);
        this.inventorySize = inventorySize;
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
    public ITextComponent getDefaultName() {
        return new StringTextComponent("Inventory " + inventorySize);
    }

    // Not used.
    @Override
    protected Container createMenu(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return inventorySize;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        this.inventorySize = ((InventoryTestBlock) state.getBlock()).getInventorySize();
        this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }
}
