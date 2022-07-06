package ellemes.container_library.api.helpers;

import ellemes.container_library.inventory.InventorySlotFunction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.ObjIntConsumer;

/**
 * @apiNote This should not be used with fabric or forge's transfer api.
 */
public final class VariableInventory implements Container {
    private final Container[] parts;
    private final int size;
    private final int maxStackCount;

    private VariableInventory(Container... parts) {
        for (int i = 0; i < parts.length; i++) {
            Objects.requireNonNull(parts[i], "part at index" + i + " must not be null");
        }
        this.parts = parts;
        this.size = Arrays.stream(parts).mapToInt(Container::getContainerSize).sum();
        this.maxStackCount = parts[0].getMaxStackSize();
        for (Container part : parts) {
            assert part.getMaxStackSize() == maxStackCount : "all parts must have equal max stack counts.";
        }
    }

    public static Container of(Container... parts) {
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
        for (Container part : parts) {
            if (!part.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        this.validateSlotIndex(slot);
        return this.applyFunctionToSlot(slot, Container::getItem);
    }

    private void validateSlotIndex(int slot) {
        assert slot >= 0 && slot < this.getContainerSize() : "slot index out of range";
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.validateSlotIndex(slot);
        return this.applyFunctionToSlot(slot, (part, rSlot) -> part.removeItem(rSlot, amount));
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.validateSlotIndex(slot);
        return this.applyFunctionToSlot(slot, Container::removeItemNoUpdate);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        this.consumeSlot(slot, (part, rSlot) -> part.setItem(rSlot, stack));
    }

    @Override
    public int getMaxStackSize() {
        return maxStackCount;
    }

    @Override
    public void setChanged() {
        for (Container part : parts) {
            part.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        for (Container part : parts) {
            if (!part.stillValid(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void startOpen(Player player) {
        for (Container part : parts) {
            part.startOpen(player);
        }
    }

    @Override
    public void stopOpen(Player player) {
        for (Container part : parts) {
            part.stopOpen(player);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        return this.applyFunctionToSlot(slot, (part, rSlot) -> part.canPlaceItem(rSlot, stack));
    }

    @Override
    public int countItem(Item item) {
        int count = 0;
        for (Container part : parts) {
            count += part.countItem(item);
        }
        return count;
    }

    @Override
    public boolean hasAnyOf(Set<Item> set) {
        for (Container part : parts) {
            if (part.hasAnyOf(set)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearContent() {
        for (Container part : parts) {
            part.clearContent();
        }
    }

    private void consumeSlot(int slot, ObjIntConsumer<Container> consumer) {
        for (Container part : parts) {
            int inventorySize = part.getContainerSize();
            if (slot >= inventorySize) {
                slot -= inventorySize;
            } else {
                consumer.accept(part, slot);
                return;
            }
        }
        throw new IllegalStateException("consumeSlot called without validating slot bounds.");
    }

    private <T> T applyFunctionToSlot(int slot, InventorySlotFunction<Container, T> function) {
        for (Container part : parts) {
            int inventorySize = part.getContainerSize();
            if (slot >= inventorySize) {
                slot -= inventorySize;
            } else {
                return function.apply(part, slot);
            }
        }
        throw new IllegalStateException("applyFunctionToSlot called without validating slot bounds.");
    }

    @SuppressWarnings("unused")
    public boolean containsPart(Container part) {
        for (Container inventory : parts) {
            if (inventory == part) {
                return true;
            }
        }
        return false;
    }
}
