package ninjaphenix.container_library.api.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.CommonMain;
import ninjaphenix.container_library.Utils;

import java.util.function.IntUnaryOperator;

public final class AbstractHandler extends ScreenHandler {
    private final Inventory inventory;

    public AbstractHandler(int syncId, Inventory inventory, PlayerInventory playerInventory) {
        super(CommonMain.getScreenHandlerType(), syncId);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        if (playerInventory.player instanceof ServerPlayerEntity) {
            for (int i = 0; i < inventory.size(); i++) {
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
    public static AbstractHandler createClientMenu(int syncId, PlayerInventory playerInventory, PacketByteBuf buffer) {
        return new AbstractHandler(syncId, new SimpleInventory(buffer.readInt()), playerInventory);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        inventory.onClose(player);
    }

    // Public API, required for mods to check if blocks should be considered open
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasStack()) {
            ItemStack newStack = slot.getStack();
            originalStack = newStack.copy();
            if (index < inventory.size()) {
                if (!this.insertItem(newStack, inventory.size(), inventory.size() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(newStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return originalStack;
    }

    // Below are client only methods
    public void resetSlotPositions(boolean createSlots, int menuWidth, int menuHeight) {
        for (int i = 0; i < inventory.size(); i++) {
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
        this.trackedStacks.clear();
    }

    public void addClientSlot(Slot slot) {
        this.addSlot(slot);
    }
}
