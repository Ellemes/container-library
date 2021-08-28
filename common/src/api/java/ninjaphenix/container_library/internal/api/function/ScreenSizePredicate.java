package ninjaphenix.container_library.internal.api.function;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ScreenSizePredicate {
    boolean test(int x, int y);

    static boolean noTest(int x, int y) {
        return false;
    }
}
