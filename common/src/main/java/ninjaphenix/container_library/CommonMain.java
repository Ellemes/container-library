package ninjaphenix.container_library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import ninjaphenix.container_library.api.client.NCL_ClientApi;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.client.gui.PageScreen;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.client.gui.ScrollScreen;
import ninjaphenix.container_library.client.gui.SingleScreen;
import ninjaphenix.container_library.inventory.ClientMenuFactory;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.NetworkWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.util.function.BiFunction;

public final class CommonMain {
    public static final Logger LOGGER = LogManager.getLogger(Utils.MOD_ID);
    private static MenuType<AbstractMenu> menuType;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initialize(BiFunction<ResourceLocation, ClientMenuFactory, MenuType> menuTypeFunction) {
        menuType = menuTypeFunction.apply(Utils.MENU_TYPE_ID, AbstractMenu::createClientMenu);

        if (PlatformUtils.isClient()) {
            ConfigWrapper.getInstance().initialise();
            NCL_ClientApi.registerScreenButton(Utils.PAGE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/paged_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.paged_screen"),
                    ScreenSizePredicate::noTest);
            NCL_ClientApi.registerScreenButton(Utils.SCROLL_SCREEN_TYPE,
                    Utils.resloc("textures/gui/scrollable_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.scrollable_screen"),
                    ScreenSizePredicate::noTest);
            NCL_ClientApi.registerScreenButton(Utils.SINGLE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/single_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.single_screen"),
                    (width, height) -> width < 370 || height < 386); // Smallest possible resolution a double netherite chest fits on.

            NCL_ClientApi.registerScreenType(Utils.PAGE_SCREEN_TYPE, PageScreen::new);
            NCL_ClientApi.registerScreenType(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::new);
            NCL_ClientApi.registerScreenType(Utils.SINGLE_SCREEN_TYPE, SingleScreen::new);

            NCL_ClientApi.registerDefaultScreenSize(Utils.PAGE_SCREEN_TYPE, (slots) -> {
                if (slots <= 27) {
                    return ScreenSize.of(9, 3);
                } else {
                    return ScreenSize.of(9, 6);
                }
            });

            NCL_ClientApi.registerDefaultScreenSize(Utils.SCROLL_SCREEN_TYPE, (slots) -> {
                if (slots <= 27) {
                    return ScreenSize.of(9, 3);
                } else {
                    return ScreenSize.of(9, 6);
                }
            });

            NCL_ClientApi.registerDefaultScreenSize(Utils.SINGLE_SCREEN_TYPE, (slots) -> {
                int width;

                if (slots <= 81) {
                    width = 9;
                } else if (slots <= 108) {
                    width = 12;
                } else if (slots <= 135) {
                    width = 15;
                } else if (slots <= 270) {
                    width = 18;
                } else {
                    throw new IllegalStateException("Cannot display single screen of size " + slots);
                }

                int height;

                if (slots <= 27) {
                    height = 3;
                } else if (slots <= 54) {
                    height = 6;
                } else if (slots <= 162) {
                    height = 9;
                } else if (slots <= 216) {
                    height = 12;
                } else if (slots <= 270) {
                    height = 15;
                } else {
                    // Never called, checked before, to silence javac errors.
                    throw new IllegalStateException("Cannot display single screen of size " + slots);
                }

                return ScreenSize.of(width, height);
            });
        }
        NetworkWrapper.getInstance().initialise();
    }

    public static void warnThrowableMessage(String message, Throwable throwable, Object... values) {
        CommonMain.LOGGER.warn(new FormattedMessage(message, values, throwable));
    }

    public static MenuType<AbstractMenu> getMenuType() {
        return menuType;
    }
}
