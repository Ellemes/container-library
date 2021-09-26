package ninjaphenix.container_library.api.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ninjaphenix.container_library.inventory.InventorySlotAccessor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public final class VariableInventory implements IInventory {
    private final IInventory[] parts;
    private final int size;
    private final int maxStackCount;

    private VariableInventory(IInventory... parts) {
        for (int i = 0; i < parts.length; i++) {
            Objects.requireNonNull(parts[i], "part at index" + i + " must not be null");
        }
        this.parts = parts;
        this.size = Arrays.stream(parts).mapToInt(IInventory::getContainerSize).sum();
        this.maxStackCount = parts[0].getMaxStackSize();
        for (IInventory part : parts) {
            assert part.getMaxStackSize() == maxStackCount : "all parts must have equal max stack counts.";
        }
    }

    public static IInventory of(IInventory... parts) {
        assert parts.length > 0 : "parts must contain at least 1 inventory";
        if (parts.length == 1) {
            return parts[0];
        } else {
            return new VariableInventory(parts);
        }
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (IInventory part : parts) {
            if (!part.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply(IInventory::getItem);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.removeItem(rSlot, amount));
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply(IInventory::removeItemNoUpdate);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        this.getPartAccessor(slot).consume((part, rSlot) -> part.setItem(rSlot, stack));
    }

    @Override
    public int getMaxStackSize() {
        return maxStackCount;
    }

    @Override
    public void setChanged() {
        for (IInventory part : parts) {
            part.setChanged();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        for (IInventory part : parts) {
            if (!part.stillValid(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void startOpen(PlayerEntity player) {
        for (IInventory part : parts) {
            part.startOpen(player);
        }
    }

    @Override
    public void stopOpen(PlayerEntity player) {
        for (IInventory part : parts) {
            part.stopOpen(player);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.canPlaceItem(rSlot, stack));
    }

    @Override
    public int countItem(Item item) {
        int count = 0;
        for (IInventory part : parts) {
            count += part.countItem(item);
        }
        return count;
    }

    @Override
    public boolean hasAnyOf(Set<Item> set) {
        for (IInventory part : parts) {
            if (part.hasAnyOf(set)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearContent() {
        for (IInventory part : parts) {
            part.clearContent();
        }
    }

    private InventorySlotAccessor<IInventory> getPartAccessor(int slot) {
        for (IInventory part : parts) {
            int inventorySize = part.getContainerSize();
            if (slot >= inventorySize) {
                slot -= inventorySize;
            } else {
                return new InventorySlotAccessor<>(part, slot);
            }
        }
        throw new IllegalStateException("getPartAccessor called without validating slot bounds.");
    }

    public boolean containsPart(IInventory part) {
        for (IInventory inventory : parts) {
            if (inventory == part) {
                return true;
            }
        }
        return false;
    }
}
