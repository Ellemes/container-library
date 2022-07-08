package ellemes.container_library.forge.client;

import com.mojang.blaze3d.platform.InputConstants;
import ellemes.container_library.Utils;
import ellemes.container_library.client.KeyHandler;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeKeyHandler implements KeyHandler {
    private final KeyMapping binding;

    public ForgeKeyHandler() {
        binding = new KeyMapping("key.ellemes_container_lib.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, Utils.KEY_BIND_KEY, "key.categories.inventory");
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterKeyMappingsEvent event) -> event.register(binding));
    }

    @Override
    public boolean isKeyPressed(int keyCode, int scanCode, int modifiers) {
        return binding.matches(keyCode, scanCode);
    }
}
