package ellemes.container_library.inventory;

public interface InventorySlotFunction<T, U> {
    U apply(T inventory, int slot);
}
