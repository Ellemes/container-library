package ellemes.container_library.fabric;

import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.fabric.client.AmecsKeyHandler;
import ellemes.container_library.fabric.client.FabricKeyHandler;
import ellemes.container_library.fabric.wrappers.ConfigWrapperImpl;
import ellemes.container_library.fabric.wrappers.NetworkWrapperImpl;
import ellemes.container_library.wrappers.PlatformUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformUtils.initialize(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? FabricLoader.getInstance().isModLoaded("amecs") ? new AmecsKeyHandler() : new FabricKeyHandler()
                : null, FabricLoader.getInstance()::isModLoaded);

        CommonMain.initialize((handlerType, factory) -> ScreenHandlerRegistry.registerExtended(handlerType, factory::create),
                FabricLoader.getInstance().getConfigDir().resolve(Utils.CONFIG_PATH),
                FabricLoader.getInstance().getConfigDir().resolve(Utils.FABRIC_LEGACY_CONFIG_PATH),
                ConfigWrapperImpl::new, new NetworkWrapperImpl());

        if (PlatformUtils.isClient()) {
            ScreenRegistry.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen);
        }
    }
}
