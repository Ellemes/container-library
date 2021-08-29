package ninjaphenix.container_library.internal.api.function;

import java.util.function.ObjIntConsumer;

@SuppressWarnings("ClassCanBeRecord")
public final class InventorySlotAccessor<T> {
    private final T inventory;
    private final int slot;

    public InventorySlotAccessor(T inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
    }

    public void consume(ObjIntConsumer<T> consumer) {
        consumer.accept(inventory, slot);
    }

    public <U> U apply(InventorySlotFunction<T, U> function) {
        return function.apply(inventory, slot);
    }
}
