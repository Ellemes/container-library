package ninjaphenix.container_library.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ForgeKeyHandler implements KeyHandler {
    private final KeyMapping key;

    public ForgeKeyHandler() {
        key = new KeyMapping("key.expandedstorage.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory");
        ClientRegistry.registerKeyBinding(key);
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return key.matches(keyCode, scanCode);
    }
}
