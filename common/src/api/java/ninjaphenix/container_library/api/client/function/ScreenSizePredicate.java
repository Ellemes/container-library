package ninjaphenix.container_library.api.client.function;

public interface ScreenSizePredicate {
    static boolean noTest(int scaledWidth, int scaledHeight) {
        return false;
    }

    boolean test(int scaledWidth, int scaledHeight);
}
