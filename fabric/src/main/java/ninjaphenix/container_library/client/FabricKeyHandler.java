package ninjaphenix.container_library.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import ninjaphenix.container_library.Utils;

public final class FabricKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public FabricKeyHandler() {
        binding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.expandedstorage.config", Utils.KEY_BIND_KEY, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode) && Screen.hasShiftDown();
    }
}
