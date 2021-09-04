package ninjaphenix.container_library.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import ninjaphenix.container_library.api.OpenableBlockEntity;

public class InventoryTestBlockEntity extends RandomizableContainerBlockEntity implements OpenableBlockEntity {
    private final int slots;
    private NonNullList<ItemStack> inventory;

    public InventoryTestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, int slots) {
        super(blockEntityType, blockPos, blockState);
        this.slots = slots;
        this.inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
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
    protected Component getDefaultName() {
        return new TextComponent("Inventory " + slots);
    }

    // Not used.
    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return slots;
    }
}
