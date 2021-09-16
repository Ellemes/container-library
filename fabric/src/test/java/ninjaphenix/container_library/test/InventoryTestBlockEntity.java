package ninjaphenix.container_library.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import ninjaphenix.container_library.api.OpenableBlockEntity;

public class InventoryTestBlockEntity extends LootableContainerBlockEntity implements OpenableBlockEntity {
    private final int inventorySize;
    private DefaultedList<ItemStack> inventory;

    public InventoryTestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.inventorySize = ((InventoryTestBlock) blockState.getBlock()).getInventorySize();
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> stacks) {
        inventory = stacks;
    }

    @Override
    protected Text getContainerName() {
        return new LiteralText("Inventory " + inventorySize);
    }

    // Not used.
    @Override
    protected ScreenHandler createScreenHandler(int i, PlayerInventory inventory) {
        return null;
    }

    @Override
    public int size() {
        return inventorySize;
    }
}
