package ninjaphenix.container_library.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return returnToScreen -> new PickScreen(NetworkWrapper.getInstance().getScreenOptions(), returnToScreen, (selection) -> {
            NetworkWrapper.getInstance().c2s_sendTypePreference(selection);
        });
    }
}
