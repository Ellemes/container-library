package ninjaphenix.container_library.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ninjaphenix.container_library.Utils;

public class ForgeKeyHandler implements KeyHandler {
    private final KeyBinding keybind;

    public ForgeKeyHandler() {
        keybind = new KeyBinding("key.expandedstorage.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputMappings.Type.KEYSYM, Utils.KEY_BIND_KEY, "key.categories.inventory");
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> ClientRegistry.registerKeyBinding(keybind));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return keybind.matches(keyCode, scanCode);
    }
}
