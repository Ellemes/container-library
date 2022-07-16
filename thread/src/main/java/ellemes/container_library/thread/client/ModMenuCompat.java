package ellemes.container_library.thread.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ellemes.container_library.client.gui.PickScreen;

public final class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return returnToScreen -> new PickScreen(() -> returnToScreen);
    }
}
