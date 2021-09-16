package ninjaphenix.container_library.client;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import ninjaphenix.container_library.Utils;
import org.lwjgl.glfw.GLFW;

public final class AmecsKeyHandler implements KeyHandler {
    private final KeyBinding keybind;

    public AmecsKeyHandler() {
        keybind = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(Utils.resloc("config"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory", new KeyModifiers().setShift(true)));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return keybind.matchesKey(keyCode, scanCode);
    }
}
