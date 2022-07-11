package ellemes.container_library.thread;

import ellemes.container_library.CommonClient;
import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.thread.client.AmecsKeyHandler;
import ellemes.container_library.thread.client.ThreadKeyHandler;
import ellemes.container_library.thread.wrappers.ConfigWrapperImpl;
import net.minecraft.client.gui.screens.MenuScreens;

import java.nio.file.Path;
import java.util.function.Function;

public class ThreadClient {
    public static void initialize(Path configDir, Function<String, Boolean> modLoadedFunction) {
        CommonClient.initialize(ConfigWrapperImpl::new, configDir.resolve(Utils.CONFIG_PATH), configDir.resolve(Utils.FABRIC_LEGACY_CONFIG_PATH),
                modLoadedFunction.apply("amecs") ? new AmecsKeyHandler() : new ThreadKeyHandler(), modLoadedFunction);
        MenuScreens.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen);
    }
}
