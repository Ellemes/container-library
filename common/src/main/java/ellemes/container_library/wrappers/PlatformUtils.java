package ellemes.container_library.wrappers;

import ellemes.container_library.client.KeyHandler;

import java.util.function.Function;

public final class PlatformUtils {
    private static boolean isClient;
    private static KeyHandler keyHandler;
    private static Function<String, Boolean> modLoadedFunction;

    public static void initialize(KeyHandler keyHandler, Function<String, Boolean> modLoadedFunction) {
        PlatformUtils.keyHandler = keyHandler;
        PlatformUtils.modLoadedFunction = modLoadedFunction;
        PlatformUtils.isClient = keyHandler != null;
    }

    public static boolean isModLoaded(String modId) {
        return modLoadedFunction.apply(modId);
    }

    // Client only.
    public static boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return keyHandler.isKeyPressed(keyCode, scanCode, modifiers);
    }

    public static boolean isClient() {
        return isClient;
    }
}
