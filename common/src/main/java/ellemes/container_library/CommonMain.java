package ellemes.container_library;

import ellemes.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.api.v2.client.NCL_ClientApiV2;
import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.ScrollScreen;
import ellemes.container_library.client.gui.SingleScreen;
import ellemes.container_library.inventory.ClientScreenHandlerFactory;
import ellemes.container_library.wrappers.ConfigWrapper;
import ellemes.container_library.wrappers.NetworkWrapper;
import ellemes.container_library.wrappers.PlatformUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

public final class CommonMain {
    public static final Logger LOGGER = LogManager.getLogger(Utils.MOD_ID);
    private static MenuType<AbstractHandler> screenHandlerType;
    private static ConfigWrapper configWrapper;
    private static NetworkWrapper networkWrapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initialize(BiFunction<ResourceLocation, ClientScreenHandlerFactory, MenuType> handlerTypeFunction, Path configPath, Path oldConfigPath,
                                  BiFunction<Path, Path, ConfigWrapper> configWrapperMaker, NetworkWrapper networkWrapper) {
        screenHandlerType = handlerTypeFunction.apply(Utils.HANDLER_TYPE_ID, AbstractHandler::createClientMenu);
        CommonMain.networkWrapper = networkWrapper;
        if (PlatformUtils.isClient()) {
            configWrapper = configWrapperMaker.apply(configPath, oldConfigPath);
            NCL_ClientApiV2.registerScreenButton(Utils.PAGE_SCREEN_TYPE,
                    Utils.id("textures/gui/page_button.png"),
                    Utils.translation("screen.ellemes_container_lib.page_screen"));
            NCL_ClientApiV2.registerScreenButton(Utils.SCROLL_SCREEN_TYPE,
                    Utils.id("textures/gui/scroll_button.png"),
                    Utils.translation("screen.ellemes_container_lib.scroll_screen"));
            NCL_ClientApiV2.registerScreenButton(Utils.SINGLE_SCREEN_TYPE,
                    Utils.id("textures/gui/single_button.png"),
                    Utils.translation("screen.ellemes_container_lib.single_screen"),
                    (scaledWidth, scaledHeight) -> scaledWidth < 370 || scaledHeight < 386, // Smallest possible resolution a double netherite chest fits on.
                    List.of(
                            Utils.translation("screen.ellemes_container_lib.off_screen_warning_1").withStyle(ChatFormatting.GRAY),
                            Utils.translation("screen.ellemes_container_lib.off_screen_warning_2").withStyle(ChatFormatting.GRAY)
                    ));

            NCL_ClientApiV2.registerScreenType(Utils.PAGE_SCREEN_TYPE, PageScreen::new);
            NCL_ClientApiV2.registerScreenType(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::new);
            NCL_ClientApiV2.registerScreenType(Utils.SINGLE_SCREEN_TYPE, SingleScreen::new);

            // todo: these settings leave no room for rei/jei should we take those into consideration for minimum screen width
            NCL_ClientApiV2.registerDefaultScreenSize(Utils.PAGE_SCREEN_TYPE, PageScreen::retrieveScreenSize);
            NCL_ClientApiV2.registerDefaultScreenSize(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::retrieveScreenSize);
            NCL_ClientApiV2.registerDefaultScreenSize(Utils.SINGLE_SCREEN_TYPE, SingleScreen::retrieveScreenSize);

            NCL_ClientApiV2.setPrefersSingleScreen(Utils.PAGE_SCREEN_TYPE);
            NCL_ClientApiV2.setPrefersSingleScreen(Utils.SCROLL_SCREEN_TYPE);
        }
    }

    public static void warnThrowableMessage(String message, Throwable throwable, Object... values) {
        CommonMain.LOGGER.warn(new FormattedMessage(message, values, throwable));
    }

    public static MenuType<AbstractHandler> getScreenHandlerType() {
        return screenHandlerType;
    }

    public static ConfigWrapper getConfigWrapper() {
        return configWrapper;
    }

    public static NetworkWrapper getNetworkWrapper() {
        return networkWrapper;
    }
}
