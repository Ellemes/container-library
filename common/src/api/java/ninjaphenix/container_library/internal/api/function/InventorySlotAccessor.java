package ninjaphenix.container_library.internal.api.function;

import java.util.function.ObjIntConsumer;

@SuppressWarnings("ClassCanBeRecord")
public final class InventorySlotAccessor<T> {
    private final T object;
    private final int number;

    public InventorySlotAccessor(T object, int number) {
        this.object = object;
        this.number = number;
    }

    public void consume(ObjIntConsumer<T> consumer) {
        consumer.accept(object, number);
    }

    public <U> U apply(InventorySlotFunction<T, U> function) {
        return function.apply(object, number);
    }
}
