package ninjaphenix.container_library;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonMain.initialize((menuType, factory) -> ScreenHandlerRegistry.registerExtended(menuType, factory::create));
    }
}
