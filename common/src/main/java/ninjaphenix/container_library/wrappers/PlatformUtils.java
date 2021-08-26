package ninjaphenix.container_library.wrappers;

import net.minecraft.client.KeyMapping;

public interface PlatformUtils {
    static PlatformUtils getInstance() {
        return PlatformUtilsImpl.getInstance();
    }

    boolean isModLoaded(String modId);

    boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers);

    boolean isClient();

    KeyMapping getConfigKey();
}
