package ninjaphenix.container_library.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;

public class InventoryTestBlockEntity extends LootableContainerBlockEntity implements OpenableBlockEntityV2 {
    private int inventorySize;
    private DefaultedList<ItemStack> inventory;

    public InventoryTestBlockEntity(BlockEntityType<?> type, int inventorySize) {
        super(type);
        this.inventorySize = inventorySize;
        this.inventory = DefaultedList.ofSize(this.inventorySize, ItemStack.EMPTY);
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

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);

        this.inventorySize = ((InventoryTestBlock) state.getBlock()).getInventorySize();
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    }
}
