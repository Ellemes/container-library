package ninjaphenix.container_library.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ninjaphenix.container_library.client.gui.PickScreen;

public final class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return returnToScreen -> new PickScreen(() -> returnToScreen, null);
    }
}
