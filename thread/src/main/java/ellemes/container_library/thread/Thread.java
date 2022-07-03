package ellemes.container_library.thread;

import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.thread.client.AmecsKeyHandler;
import ellemes.container_library.thread.client.ThreadKeyHandler;
import ellemes.container_library.thread.wrappers.ConfigWrapperImpl;
import ellemes.container_library.thread.wrappers.NetworkWrapperImpl;
import ellemes.container_library.wrappers.PlatformUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.nio.file.Path;
import java.util.function.Function;

public class Thread {
    public static void initialize(boolean isClient, Function<String, Boolean> isModLoaded, Path configDir) {
        PlatformUtils.initialize(
                isClient ? isModLoaded.apply("amecs") ? new AmecsKeyHandler() : new ThreadKeyHandler() : null,
                isModLoaded
        );

        CommonMain.initialize(isClient, (handlerType, factory) -> {
                    MenuType<AbstractContainerMenu> type = new ExtendedScreenHandlerType<>(factory::create);
                    return Registry.register(Registry.MENU, handlerType, type);
                },
                configDir.resolve(Utils.CONFIG_PATH),
                configDir.resolve(Utils.FABRIC_LEGACY_CONFIG_PATH),
                ConfigWrapperImpl::new, new NetworkWrapperImpl(isModLoaded.apply("flan")));

        if (isClient) {
            MenuScreens.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen);
        }
    }
}
