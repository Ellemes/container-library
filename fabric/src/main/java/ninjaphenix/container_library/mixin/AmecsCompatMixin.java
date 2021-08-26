package ninjaphenix.container_library.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import ninjaphenix.container_library.wrappers.PlatformUtilsImpl;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// todo: should find alternative as mixins aren't guaranteed to apply to mod classes.
@Mixin(PlatformUtilsImpl.class)
public abstract class AmecsCompatMixin implements PlatformUtils {
    /**
     * @author NinjaPhenix
     * @reason To support Amecs custom key mapping
     */
    @Overwrite(remap = false)
    private Object createConfigKey() {
        KeyModifiers modifiers = new KeyModifiers();
        AmecsKeyBinding keyMapping = new AmecsKeyBinding(Utils.resloc("config"), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory", modifiers);
        return KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    /**
     * @author NinjaPhenix
     * @reason To support Amecs custom key mapping
     */
    @Override
    @Overwrite(remap = false)
    public boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getConfigKey().matches(keyCode, scanCode);
    }
}
