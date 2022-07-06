package ellemes.container_library;

import ellemes.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.api.v3.client.ScreenTypeApi;
import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.ScrollScreen;
import ellemes.container_library.client.gui.SingleScreen;
import ellemes.container_library.inventory.ClientScreenHandlerFactory;
import ellemes.container_library.wrappers.ConfigWrapper;
import ellemes.container_library.wrappers.NetworkWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
    public static void initialize(boolean isClient, BiFunction<ResourceLocation, ClientScreenHandlerFactory, MenuType> handlerTypeFunction, Path configPath, Path oldConfigPath,
                                  BiFunction<Path, Path, ConfigWrapper> configWrapperMaker, NetworkWrapper networkWrapper) {
        screenHandlerType = handlerTypeFunction.apply(Utils.HANDLER_TYPE_ID, AbstractHandler::createClientMenu);
        CommonMain.networkWrapper = networkWrapper;
        if (isClient) {
            configWrapper = configWrapperMaker.apply(configPath, oldConfigPath);
            ScreenTypeApi.registerScreenButton(Utils.PAGE_SCREEN_TYPE,
                    Utils.id("textures/gui/page_button.png"),
                    Component.translatable("screen.ellemes_container_lib.page_screen"));
            ScreenTypeApi.registerScreenButton(Utils.SCROLL_SCREEN_TYPE,
                    Utils.id("textures/gui/scroll_button.png"),
                    Component.translatable("screen.ellemes_container_lib.scroll_screen"));
            ScreenTypeApi.registerScreenButton(Utils.SINGLE_SCREEN_TYPE,
                    Utils.id("textures/gui/single_button.png"),
                    Component.translatable("screen.ellemes_container_lib.single_screen"),
                    (scaledWidth, scaledHeight) -> scaledWidth < 370 || scaledHeight < 386, // Smallest possible resolution a double netherite chest fits on.
                    List.of(
                            Component.translatable("screen.ellemes_container_lib.off_screen_warning_1").withStyle(ChatFormatting.GRAY),
                            Component.translatable("screen.ellemes_container_lib.off_screen_warning_2").withStyle(ChatFormatting.GRAY)
                    ));

            ScreenTypeApi.registerScreenType(Utils.PAGE_SCREEN_TYPE, PageScreen::new);
            ScreenTypeApi.registerScreenType(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::new);
            ScreenTypeApi.registerScreenType(Utils.SINGLE_SCREEN_TYPE, SingleScreen::new);

            // todo: these settings leave no room for rei/jei should we take those into consideration for minimum screen width
            ScreenTypeApi.registerDefaultScreenSize(Utils.PAGE_SCREEN_TYPE, PageScreen::retrieveScreenSize);
            ScreenTypeApi.registerDefaultScreenSize(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::retrieveScreenSize);
            ScreenTypeApi.registerDefaultScreenSize(Utils.SINGLE_SCREEN_TYPE, SingleScreen::retrieveScreenSize);

            ScreenTypeApi.setPrefersSingleScreen(Utils.PAGE_SCREEN_TYPE);
            ScreenTypeApi.setPrefersSingleScreen(Utils.SCROLL_SCREEN_TYPE);
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
