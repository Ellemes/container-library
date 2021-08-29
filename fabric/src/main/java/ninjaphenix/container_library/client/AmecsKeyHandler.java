package ninjaphenix.container_library.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import ninjaphenix.container_library.Utils;
import org.lwjgl.glfw.GLFW;

public final class AmecsKeyHandler implements KeyHandler {
    private final KeyMapping key;

    public AmecsKeyHandler() {
       key = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(Utils.resloc("config"), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory", new KeyModifiers().setShift(true)));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return key.matches(keyCode, scanCode);
    }
}
