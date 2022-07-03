package ellemes.container_library.thread.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import ellemes.container_library.Utils;
import ellemes.container_library.client.KeyHandler;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

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
