package ninjaphenix.container_library.internal.api.function;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ScreenSizePredicate {
    static boolean noTest(int scaledWidth, int scaledHeight) {
        return false;
    }

    boolean test(int scaledWidth, int scaledHeight);
}
