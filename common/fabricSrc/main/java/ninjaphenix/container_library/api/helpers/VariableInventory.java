package ninjaphenix.container_library.api.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ninjaphenix.container_library.inventory.InventorySlotAccessor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public final class VariableInventory implements Inventory {
    private final Inventory[] parts;
    private final int size;
    private final int maxStackCount;

    private VariableInventory(Inventory... parts) {
        for (int i = 0; i < parts.length; i++) {
            Objects.requireNonNull(parts[i], "part at index" + i + " must not be null");
        }
        this.parts = parts;
        this.size = Arrays.stream(parts).mapToInt(Inventory::size).sum();
        this.maxStackCount = parts[0].getMaxCountPerStack();
        for (Inventory part : parts) {
            assert part.getMaxCountPerStack() == maxStackCount : "all parts must have equal max stack counts.";
        }
    }

    public static Inventory of(Inventory... parts) {
        assert parts.length > 0 : "parts must contain at least 1 inventory";
        if (parts.length == 1) {
            return parts[0];
        } else {
            return new VariableInventory(parts);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Inventory part : parts) {
            if (!part.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        assert slot >= 0 && slot < this.size() : "slot index out of range";
        return this.getPartAccessor(slot).apply(Inventory::getStack);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        assert slot >= 0 && slot < this.size() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.removeStack(rSlot, amount));
    }

    @Override
    public ItemStack removeStack(int slot) {
        assert slot >= 0 && slot < this.size() : "slot index out of range";
        return this.getPartAccessor(slot).apply(Inventory::removeStack);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        assert slot >= 0 && slot < this.size() : "slot index out of range";
        this.getPartAccessor(slot).consume((part, rSlot) -> part.setStack(rSlot, stack));
    }

    @Override
    public int getMaxCountPerStack() {
        return maxStackCount;
    }

    @Override
    public void markDirty() {
        for (Inventory part : parts) {
            part.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        for (Inventory part : parts) {
            if (!part.canPlayerUse(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        for (Inventory part : parts) {
            part.onOpen(player);
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        for (Inventory part : parts) {
            part.onClose(player);
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        assert slot >= 0 && slot < this.size() : "slot index out of range";
        return this.getPartAccessor(slot).apply((part, rSlot) -> part.isValid(rSlot, stack));
    }

    @Override
    public int count(Item item) {
        int count = 0;
        for (Inventory part : parts) {
            count += part.count(item);
        }
        return count;
    }

    @Override
    public boolean containsAny(Set<Item> set) {
        for (Inventory part : parts) {
            if (part.containsAny(set)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (Inventory part : parts) {
            part.clear();
        }
    }

    private InventorySlotAccessor<Inventory> getPartAccessor(int slot) {
        for (Inventory part : parts) {
            int inventorySize = part.size();
            if (slot >= inventorySize) {
                slot -= inventorySize;
            } else {
                return new InventorySlotAccessor<>(part, slot);
            }
        }
        throw new IllegalStateException("getPartAccessor called without validating slot bounds.");
    }

    public boolean containsPart(Inventory part) {
        for (Inventory inventory : parts) {
            if (inventory == part) {
                return true;
            }
        }
        return false;
    }
}
