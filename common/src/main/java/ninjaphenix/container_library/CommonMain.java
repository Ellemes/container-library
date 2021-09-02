package ninjaphenix.container_library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.internal.api.function.ScreenSizePredicate;
import ninjaphenix.container_library.internal.api.inventory.ClientMenuFactory;
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

        if (PlatformUtils.getInstance().isClient()) {
            ConfigWrapper.getInstance().initialise();
            PickScreen.declareButtonSettings(Utils.PAGE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/paged_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.paged_screen"),
                    ScreenSizePredicate::noTest);
            PickScreen.declareButtonSettings(Utils.SCROLL_SCREEN_TYPE,
                    Utils.resloc("textures/gui/scrollable_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.scrollable_screen"),
                    ScreenSizePredicate::noTest);
            PickScreen.declareButtonSettings(Utils.SINGLE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/single_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.single_screen"),
                    (width, height) -> width < 370 || height < 386); // Smallest possible resolution a double netherite chest fits on.
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
