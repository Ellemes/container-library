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

import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;

public class InventoryTestBlockEntity extends LootableContainerBlockEntity implements OpenableBlockEntityV2 {
    private final int inventorySize;
    private DefaultedList<ItemStack> inventory;

    public InventoryTestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventorySize = ((InventoryTestBlock) state.getBlock()).getInventorySize();
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
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int size() {
        return inventorySize;
    }
}
