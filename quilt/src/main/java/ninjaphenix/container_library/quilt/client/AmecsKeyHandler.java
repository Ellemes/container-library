package ninjaphenix.container_library.quilt.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.KeyHandler;

public final class AmecsKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public AmecsKeyHandler() {
        binding = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(Utils.id("config"), InputConstants.Type.KEYSYM, Utils.KEY_BIND_KEY, "key.categories.inventory", new KeyModifiers().setShift(true)));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode);
    }
}
