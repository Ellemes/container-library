package ninjaphenix.container_library;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.client.AmecsKeyHandler;
import ninjaphenix.container_library.client.FabricKeyHandler;
import ninjaphenix.container_library.wrappers.PlatformUtils;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        PlatformUtils.initialize(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? FabricLoader.getInstance().isModLoaded("amecs") ? new AmecsKeyHandler() : new FabricKeyHandler()
                : null, FabricLoader.getInstance()::isModLoaded);

        CommonMain.initialize((handlerType, factory) -> ScreenHandlerRegistry.registerExtended(handlerType, factory::create),
                FabricLoader.getInstance().getConfigDir().resolve(Utils.CONFIG_PATH),
                FabricLoader.getInstance().getConfigDir().resolve(Utils.FABRIC_LEGACY_CONFIG_PATH));

        if (PlatformUtils.isClient()) {
            //noinspection deprecation
            ScreenRegistry.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen);
        }

        try {
            ((ModInitializer) Class.forName("ninjaphenix.container_library.test.Main").newInstance()).onInitialize();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // Not an error just weird test set-up
        }
    }
}
