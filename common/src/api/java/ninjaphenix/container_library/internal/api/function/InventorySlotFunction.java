package ninjaphenix.container_library.internal.api.function;

public interface InventorySlotFunction<T, U> {
    U apply(T object, int number);
}
