package ninjaphenix.container_library.forge.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.KeyHandler;

public class ForgeKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public ForgeKeyHandler() {
        binding = new KeyMapping("key.expandedstorage.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, Utils.KEY_BIND_KEY, "key.categories.inventory");
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> ClientRegistry.registerKeyBinding(binding));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode);
    }
}
