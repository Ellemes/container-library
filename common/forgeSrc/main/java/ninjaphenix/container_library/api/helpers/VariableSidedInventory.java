package ninjaphenix.container_library.api.helpers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import ninjaphenix.container_library.inventory.InventorySlotAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class VariableSidedInventory implements ISidedInventory {
    private final ISidedInventory[] parts;
    private final int size;
    private final int maxStackCount;
    private final Map<Direction, int[]> slotsAccessibleThroughFace = new HashMap<>();

    private VariableSidedInventory(ISidedInventory... parts) {
        for (int i = 0; i < parts.length; i++) {
            assert parts[i] != null : "part at index " + i + " must not be null";
        }
        this.parts = parts;
        this.size = Arrays.stream(parts).mapToInt(ISidedInventory::getContainerSize).sum();
        this.maxStackCount = parts[0].getMaxStackSize();
        for (ISidedInventory part : parts) {
            assert part.getMaxStackSize() == maxStackCount : "all parts must have equal max stack counts.";
        }
    }

    public static ISidedInventory of(ISidedInventory... parts) {
        assert parts.length > 0 : "parts must contain at least 1 inventory";
        if (parts.length == 1) {
            return parts[0];
        } else {
            return new VariableSidedInventory(parts);
        }
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (ISidedInventory part : parts) {
            if (!part.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply(ISidedInventory::getItem);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.removeItem(rSlot, amount));
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply(ISidedInventory::removeItemNoUpdate);
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
        for (ISidedInventory part : parts) {
            part.setChanged();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        for (ISidedInventory part : parts) {
            if (!part.stillValid(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void startOpen(PlayerEntity player) {
        for (ISidedInventory part : parts) {
            part.startOpen(player);
        }
    }

    @Override
    public void stopOpen(PlayerEntity player) {
        for (ISidedInventory part : parts) {
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
        for (ISidedInventory part : parts) {
            count += part.countItem(item);
        }
        return count;
    }

    @Override
    public boolean hasAnyOf(Set<Item> set) {
        for (ISidedInventory part : parts) {
            if (part.hasAnyOf(set)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearContent() {
        for (ISidedInventory part : parts) {
            part.clearContent();
        }
    }

    private InventorySlotAccessor<ISidedInventory> getPartAccessor(int slot) {
        for (ISidedInventory part : parts) {
            int inventorySize = part.getContainerSize();
            if (slot >= inventorySize) {
                slot -= inventorySize;
            } else {
                return new InventorySlotAccessor<>(part, slot);
            }
        }
        throw new IllegalStateException("getPartAccessor called without validating slot bounds.");
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return slotsAccessibleThroughFace.computeIfAbsent(direction, (dir) -> {
            int previousSize = 0;
            IntArrayList list = new IntArrayList();
            for (ISidedInventory part : parts) {
                for (int i : part.getSlotsForFace(dir)) {
                    list.add(i + previousSize);
                }
                previousSize += part.getContainerSize();
            }
            return list.toIntArray();
        });
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.canPlaceItemThroughFace(rSlot, stack, direction));
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.canTakeItemThroughFace(rSlot, stack, direction));
    }

    public boolean containsPart(ISidedInventory part) {
        for (ISidedInventory inventory : parts) {
            if (inventory == part) {
                return true;
            }
        }
        return false;
    }
}
