package ninjaphenix.container_library;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import ninjaphenix.container_library.client.gui.PageScreen;
import ninjaphenix.container_library.client.gui.ScrollScreen;
import ninjaphenix.container_library.client.gui.SingleScreen;
import ninjaphenix.container_library.wrappers.PlatformUtils;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonMain.initialize((menuType, factory) -> ScreenHandlerRegistry.registerExtended(menuType, factory::create));

        if (PlatformUtils.getInstance().isClient()) {
            ScreenRegistry.register(CommonMain.getScrollMenuType(), ScrollScreen::new);
            ScreenRegistry.register(CommonMain.getPageMenuType(), PageScreen::new);
            ScreenRegistry.register(CommonMain.getSingleMenuType(), SingleScreen::new);
            PlatformUtils.getInstance().getConfigKey(); // Ensure config key is registered.
        }
    }
}
