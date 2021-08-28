package ninjaphenix.container_library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.internal.api.function.ScreenSizePredicate;
import ninjaphenix.container_library.internal.api.inventory.AbstractMenu;
import ninjaphenix.container_library.internal.api.inventory.ClientMenuFactory;
import ninjaphenix.container_library.inventory.PageMenu;
import ninjaphenix.container_library.inventory.ScrollMenu;
import ninjaphenix.container_library.inventory.SingleMenu;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.NetworkWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.util.function.BiFunction;

public final class CommonMain {
    public static final Logger LOGGER = LogManager.getLogger(Utils.MOD_ID);
    private static MenuType<PageMenu> pageMenuType;
    private static MenuType<ScrollMenu> scrollMenuType;
    private static MenuType<SingleMenu> singleMenuType;

    public static MenuType<PageMenu> getPageMenuType() {
        return pageMenuType;
    }

    public static MenuType<ScrollMenu> getScrollMenuType() {
        return scrollMenuType;
    }

    public static MenuType<SingleMenu> getSingleMenuType() {
        return singleMenuType;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends AbstractMenu<?>> void initialize(BiFunction<ResourceLocation, ClientMenuFactory, MenuType> menuTypeFunction) {
        pageMenuType = menuTypeFunction.apply(Utils.PAGE_SCREEN_TYPE, new PageMenu.Factory());
        scrollMenuType = menuTypeFunction.apply(Utils.SCROLL_SCREEN_TYPE, new ScrollMenu.Factory());
        singleMenuType = menuTypeFunction.apply(Utils.SINGLE_SCREEN_TYPE, new SingleMenu.Factory());

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
}
