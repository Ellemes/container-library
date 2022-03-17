package ninjaphenix.container_library;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.client.AmecsKeyHandler;
import ninjaphenix.container_library.client.FabricKeyHandler;
import ninjaphenix.container_library.wrappers.ConfigWrapperImpl;
import ninjaphenix.container_library.wrappers.NetworkWrapperImpl;
import ninjaphenix.container_library.wrappers.PlatformUtils;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformUtils.initialize(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? FabricLoader.getInstance().isModLoaded("amecs") ? new AmecsKeyHandler() : new FabricKeyHandler()
                : null, FabricLoader.getInstance()::isModLoaded);

        CommonMain.init(FabricLoader.getInstance().getConfigDir().resolve(Utils.CONFIG_PATH),
                FabricLoader.getInstance().getConfigDir().resolve(Utils.FABRIC_LEGACY_CONFIG_PATH),
                ConfigWrapperImpl::new, new NetworkWrapperImpl());
    }
}
