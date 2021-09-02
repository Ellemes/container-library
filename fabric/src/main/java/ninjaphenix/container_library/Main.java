package ninjaphenix.container_library;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.client.gui.PageScreen;
import ninjaphenix.container_library.client.gui.ScrollScreen;
import ninjaphenix.container_library.client.gui.SingleScreen;
import ninjaphenix.container_library.client.AmecsKeyHandler;
import ninjaphenix.container_library.client.FabricKeyHandler;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        new PlatformUtils(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? FabricLoader.getInstance().isModLoaded("amecs") ? new AmecsKeyHandler() : new FabricKeyHandler()
                : null, FabricLoader.getInstance()::isModLoaded);

        CommonMain.initialize((menuType, factory) -> ScreenHandlerRegistry.registerExtended(menuType, factory::create));

        if (PlatformUtils.getInstance().isClient()) {
            ScreenRegistry.register(CommonMain.getMenuType(), (menu, inventory, title) -> {
                ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
                if (preference == Utils.PAGE_SCREEN_TYPE) {
                    return new PageScreen(menu, inventory, title);
                } else if (preference == Utils.SCROLL_SCREEN_TYPE) {
                    return new ScrollScreen(menu, inventory, title);
                } else if (preference == Utils.SINGLE_SCREEN_TYPE) {
                    return new SingleScreen(menu, inventory, title);
                }
                // Should be an illegal state.
                return null;
            });
        }
    }
}
