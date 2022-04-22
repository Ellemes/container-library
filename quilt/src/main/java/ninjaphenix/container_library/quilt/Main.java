package ninjaphenix.container_library.quilt;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import ninjaphenix.container_library.CommonMain;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.quilt.client.AmecsKeyHandler;
import ninjaphenix.container_library.quilt.client.QuiltKeyHandler;
import ninjaphenix.container_library.quilt.wrappers.ConfigWrapperImpl;
import ninjaphenix.container_library.quilt.wrappers.NetworkWrapperImpl;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        PlatformUtils.initialize(MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT
                ? QuiltLoader.isModLoaded("amecs") ? new AmecsKeyHandler() : new QuiltKeyHandler()
                : null, QuiltLoader::isModLoaded);

        CommonMain.initialize((handlerType, factory) -> ScreenHandlerRegistry.registerExtended(handlerType, factory::create),
                QuiltLoader.getConfigDir().resolve(Utils.CONFIG_PATH),
                QuiltLoader.getConfigDir().resolve(Utils.FABRIC_LEGACY_CONFIG_PATH),
                ConfigWrapperImpl::new, new NetworkWrapperImpl());

        if (PlatformUtils.isClient()) {
            //noinspection deprecation
            ScreenRegistry.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen);
        }
    }
}
