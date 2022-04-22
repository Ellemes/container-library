package ninjaphenix.container_library.quilt.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.KeyHandler;

public final class QuiltKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public QuiltKeyHandler() {
        binding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.expandedstorage.config", Utils.KEY_BIND_KEY, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode) && Screen.hasShiftDown();
    }
}
