package ninjaphenix.container_library.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import ninjaphenix.container_library.Utils;

public final class FabricKeyHandler implements KeyHandler {
    private final KeyBinding keybind;

    public FabricKeyHandler() {
        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.expandedstorage.config", Utils.KEY_BIND_KEY, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        var windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        return keybind.matchesKey(keyCode, scanCode) &&
                (InputUtil.isKeyPressed(windowHandle, InputUtil.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(windowHandle, InputUtil.GLFW_KEY_RIGHT_SHIFT));
    }
}
