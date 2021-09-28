package ninjaphenix.container_library.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import ninjaphenix.container_library.Utils;

public final class FabricKeyHandler implements KeyHandler {
    private final KeyBinding keybind;

    public FabricKeyHandler() {
        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.expandedstorage.config", Utils.KEY_BIND_KEY, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return keybind.matchesKey(keyCode, scanCode) && Screen.hasShiftDown();
    }
}
