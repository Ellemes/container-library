package ninjaphenix.container_library.wrappers;

import ninjaphenix.container_library.client.KeyHandler;

import java.util.function.Function;

public final class PlatformUtils {
    private static PlatformUtils INSTANCE;

    private final boolean isClient;
    private final KeyHandler keyHandler;
    private final Function<String, Boolean> modLoadedFunction;

    public PlatformUtils(KeyHandler keyHandler, Function<String, Boolean> modLoadedFunction) {
        this.keyHandler = keyHandler;
        this.modLoadedFunction = modLoadedFunction;
        isClient = keyHandler != null;

        if (PlatformUtils.INSTANCE != null) {
            throw new IllegalStateException("Tried constructing 2 instances of a singleton");
        }
        PlatformUtils.INSTANCE = this;
    }

    public static PlatformUtils getInstance() {
        return PlatformUtils.INSTANCE;
    }

    public boolean isModLoaded(String modId) {
        return modLoadedFunction.apply(modId);
    }

    // Client only.
    public boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return keyHandler.isKeyPressed(keyCode, scanCode, modifiers);
    }

    public boolean isClient() {
        return isClient;
    }
}
