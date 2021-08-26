package ninjaphenix.container_library.wrappers;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private final Supplier<Object> configKey = Suppliers.memoize(() -> {
        KeyMapping binding = new KeyMapping("key.expandedstorage.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory");
        ClientRegistry.registerKeyBinding(binding);
        return binding;
    });
    private final boolean isClient;

    private PlatformUtilsImpl() {
        isClient = FMLLoader.getDist() == Dist.CLIENT;
    }

    public static PlatformUtilsImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformUtilsImpl();
        }
        return INSTANCE;
    }

    @Override
    public boolean isClient() {
        return isClient;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public KeyMapping getConfigKey() {
        return (KeyMapping) configKey.get();
    }

    @Override
    public boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return getConfigKey().isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }
}
