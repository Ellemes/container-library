package ninjaphenix.container_library.api.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.CommonMain;
import ninjaphenix.container_library.Utils;

import java.util.function.IntUnaryOperator;

public final class AbstractHandler extends Container {
    private final IInventory inventory;

    public AbstractHandler(int syncId, IInventory inventory, PlayerInventory playerInventory) {
        super(CommonMain.getScreenHandlerType(), syncId);
        this.inventory = inventory;
        inventory.startOpen(playerInventory.player);
        if (playerInventory.player instanceof ServerPlayerEntity) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                this.addSlot(new Slot(inventory, i, i * Utils.SLOT_SIZE, 0));
            }
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 3; y++) {
                    this.addSlot(new Slot(playerInventory, y * 9 + x + 9, Utils.SLOT_SIZE * x, y * Utils.SLOT_SIZE));
                }
            }
            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(playerInventory, i, i * Utils.SLOT_SIZE, 2 * Utils.SLOT_SIZE));
            }
        }
    }

    // Client only
    public static AbstractHandler createClientMenu(int syncId, PlayerInventory playerInventory, PacketBuffer buffer) {
        return new AbstractHandler(syncId, new Inventory(buffer.readInt()), playerInventory);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return inventory.stillValid(player);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        inventory.stopOpen(player);
    }

    // Public API, required for mods to check if blocks should be considered open
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack newStack = slot.getItem();
            originalStack = newStack.copy();
            if (index < inventory.getContainerSize()) {
                if (!this.moveItemStackTo(newStack, inventory.getContainerSize(), inventory.getContainerSize() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(newStack, 0, inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return originalStack;
    }

    // Below are client only methods
    public void resetSlotPositions(boolean createSlots, int menuWidth, int menuHeight) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            int slotXPos = i % menuWidth;
            int slotYPos = MathHelper.ceil((((double) (i - slotXPos)) / menuWidth));
            int realYPos = slotYPos >= menuHeight ? (Utils.SLOT_SIZE * (slotYPos % menuHeight)) - 2000 : slotYPos * Utils.SLOT_SIZE;
            if (createSlots) {
                this.addSlot(new Slot(inventory, i, slotXPos * Utils.SLOT_SIZE + 8, realYPos + Utils.SLOT_SIZE));
            } else {
                slots.get(i).y = realYPos + Utils.SLOT_SIZE;
            }
        }
    }

    public void moveSlotRange(int minSlotIndex, int maxSlotIndex, int yDifference) {
        for (int i = minSlotIndex; i < maxSlotIndex; i++) {
            slots.get(i).y += yDifference;
        }
    }

    public void setSlotRange(int minSlotIndex, int maxSlotIndex, IntUnaryOperator yMutator) {
        for (int i = minSlotIndex; i < maxSlotIndex; i++) {
            slots.get(i).y = yMutator.applyAsInt(i);
        }
    }

    public void clearSlots() {
        this.slots.clear();
        this.lastSlots.clear();
    }

    public void addClientSlot(Slot slot) {
        this.addSlot(slot);
    }
}
