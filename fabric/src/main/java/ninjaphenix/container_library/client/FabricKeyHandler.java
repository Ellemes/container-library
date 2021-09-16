package ninjaphenix.container_library.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class FabricKeyHandler implements KeyHandler {
    private final KeyBinding key;

    public FabricKeyHandler() {
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.expandedstorage.config", GLFW.GLFW_KEY_W, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return key.matchesKey(keyCode, scanCode) && (modifiers & 1) > 0;
    }
}
