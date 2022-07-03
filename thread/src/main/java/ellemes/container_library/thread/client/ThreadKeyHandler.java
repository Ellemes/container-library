package ellemes.container_library.thread.client;

import ellemes.container_library.Utils;
import ellemes.container_library.client.KeyHandler;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;

public final class ThreadKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public ThreadKeyHandler() {
        binding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ellemes_container_lib.config", Utils.KEY_BIND_KEY, "key.categories.inventory"));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode) && Screen.hasShiftDown();
    }
}
