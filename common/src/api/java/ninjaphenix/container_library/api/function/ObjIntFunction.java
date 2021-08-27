package ninjaphenix.container_library.api.function;

public interface ObjIntFunction<T, U> {
    U get(T object, int number);
}
