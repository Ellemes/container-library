package ninjaphenix.container_library.api.function;

import java.util.function.ObjIntConsumer;

@SuppressWarnings("ClassCanBeRecord")
public final class ObjIntSupplier<T> {
    private final T object;
    private final int number;

    public ObjIntSupplier(T object, int number) {
        this.object = object;
        this.number = number;
    }

    public void consume(ObjIntConsumer<T> consumer) {
        consumer.accept(object, number);
    }

    public <U> U apply(ObjIntFunction<T, U> function) {
        return function.apply(object, number);
    }
}
