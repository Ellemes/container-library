package ninjaphenix.container_library.wrappers;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private final boolean isClient;
    private final Supplier<Object> configKey = Suppliers.memoize(this::createConfigKey);

    private PlatformUtilsImpl() {
        isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static PlatformUtilsImpl getInstance() {
        if (PlatformUtilsImpl.INSTANCE == null) {
            PlatformUtilsImpl.INSTANCE = new PlatformUtilsImpl();
        }
        return PlatformUtilsImpl.INSTANCE;
    }

    private Object createConfigKey() {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping("key.expandedstorage.config", GLFW.GLFW_KEY_W, "key.categories.inventory"));
    }

    @Override
    public boolean isClient() {
        return isClient;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getConfigKey().matches(keyCode, scanCode) && (modifiers & 1) > 0;
    }

    @Environment(EnvType.CLIENT)
    public KeyMapping getConfigKey() {
        return (KeyMapping) configKey.get();
    }
}
