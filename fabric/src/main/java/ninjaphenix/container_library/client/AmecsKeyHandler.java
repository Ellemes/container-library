package ninjaphenix.container_library.client;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import ninjaphenix.container_library.Utils;

public final class AmecsKeyHandler implements KeyHandler {
    private final KeyBinding binding;

    public AmecsKeyHandler() {
        binding = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(Utils.id("config"), InputUtil.Type.KEYSYM, Utils.KEY_BIND_KEY, "key.categories.inventory", new KeyModifiers().setShift(true)));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matchesKey(keyCode, scanCode);
    }
}
