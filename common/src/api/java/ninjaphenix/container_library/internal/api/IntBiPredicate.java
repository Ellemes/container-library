package ninjaphenix.container_library.internal.api;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IntBiPredicate {
    boolean test(int x, int y);

    static boolean never(int x, int y) {
        return false;
    }
}
