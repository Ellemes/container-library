package ninjaphenix.container_library;

import com.mojang.datafixers.util.Pair;
import ninjaphenix.container_library.api.client.NCL_ClientApi;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.client.gui.PageScreen;
import ninjaphenix.container_library.client.gui.ScrollScreen;
import ninjaphenix.container_library.client.gui.SingleScreen;
import ninjaphenix.container_library.inventory.ClientMenuFactory;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.NetworkWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.MenuType;

public final class CommonMain {
    public static final Logger LOGGER = LogManager.getLogger(Utils.MOD_ID);
    private static MenuType<AbstractMenu> menuType;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initialize(BiFunction<ResourceLocation, ClientMenuFactory, MenuType> menuTypeFunction,
                                  Path configPath,
                                  Path oldConfigPath) {
        menuType = menuTypeFunction.apply(Utils.MENU_TYPE_ID, AbstractMenu::createClientMenu);

        if (PlatformUtils.isClient()) {
            ConfigWrapper.getInstance().initialise(configPath, oldConfigPath);
            NCL_ClientApi.registerScreenButton(Utils.PAGE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/paged_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.paged_screen"));
            NCL_ClientApi.registerScreenButton(Utils.SCROLL_SCREEN_TYPE,
                    Utils.resloc("textures/gui/scrollable_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.scrollable_screen"));
            NCL_ClientApi.registerScreenButton(Utils.SINGLE_SCREEN_TYPE,
                    Utils.resloc("textures/gui/single_button.png"),
                    Utils.translation("screen.ninjaphenix_container_lib.single_screen"),
                    (width, height) -> width < 370 || height < 386, // Smallest possible resolution a double netherite chest fits on.
                    List.of(
                            Utils.translation("screen.ninjaphenix_container_lib.off_screen_warning_1").withStyle(ChatFormatting.GRAY),
                            Utils.translation("screen.ninjaphenix_container_lib.off_screen_warning_2").withStyle(ChatFormatting.GRAY)
                    ));

            NCL_ClientApi.registerScreenType(Utils.PAGE_SCREEN_TYPE, PageScreen::new);
            NCL_ClientApi.registerScreenType(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::new);
            NCL_ClientApi.registerScreenType(Utils.SINGLE_SCREEN_TYPE, SingleScreen::new);

            // todo: these settings leave no room for rei/jei should we take those into consideration for minimum screen width / height
            ScreenSizeRetriever pageRetriever = (slots, scaledWidth, scaledHeight) -> {
                ArrayList<Pair<ScreenSize, ScreenSize>> options = new ArrayList<>();
                CommonMain.addEntry(options, slots, 9, 3);
                CommonMain.addEntry(options, slots, 9, 6);
                if (scaledHeight >= 276) {
                    CommonMain.addEntry(options, slots, 9, 9);
                }
                Pair<ScreenSize, ScreenSize> picked = null;
                for (Pair<ScreenSize, ScreenSize> option : options) {
                    if (picked == null) {
                        picked = option;
                    } else {
                        ScreenSize pickedMeta = picked.getSecond();
                        ScreenSize iterMeta = option.getSecond();
                        ScreenSize iterDim = option.getFirst();
                        if (pickedMeta.getHeight() == iterMeta.getHeight() && iterMeta.getWidth() < pickedMeta.getWidth()) {
                            picked = option;
                        } else if (ConfigWrapper.getInstance().preferSmallerScreens() && pickedMeta.getWidth() == iterMeta.getWidth() + 1 && iterMeta.getHeight() <= iterDim.getWidth() * iterDim.getHeight() / 2.0) {

                        } else if (iterMeta.getWidth() < pickedMeta.getWidth() && iterMeta.getHeight() <= iterDim.getWidth() * iterDim.getHeight() / 2.0) {
                            picked = option;
                        }
                    }
                }
                return picked.getFirst();
            };

            ScreenSizeRetriever scrollRetriever = (slots, scaledWidth, scaledHeight) -> {
                int width = 9;
                int height = 6;
                if (slots <= 27) {
                    height = 3;
                } else if (scaledHeight >= 276) {
                    if (slots > 54) {
                        height = 9;
                        //if (scaledWidth >= 338 && slots > 135) {
                        //    width = 18;
                        //} else if (slots > 108) {
                        //    width = 15;
                        //} else if (slots > 81) {
                        //    width = 12;
                        //}
                    }
                }
                return ScreenSize.of(width, height);
            };

            NCL_ClientApi.registerDefaultScreenSize(Utils.PAGE_SCREEN_TYPE, pageRetriever);
            NCL_ClientApi.registerDefaultScreenSize(Utils.SCROLL_SCREEN_TYPE, scrollRetriever);

            NCL_ClientApi.registerDefaultScreenSize(Utils.SINGLE_SCREEN_TYPE, (slots, scaledWidth, scaledHeight) -> {
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
                } else /* if (slots <= 270) */ {
                    height = 15;
                } // slots is guaranteed to be 270 or below when getting width.

                return ScreenSize.of(width, height);
            });
        }
        NetworkWrapper.getInstance().initialise();
    }

    private static void addEntry(ArrayList<Pair<ScreenSize, ScreenSize>> options, int slots, int width, int height) {
        int pages = Mth.ceil((double) slots / (width * height));
        int blanked = slots - pages * width * height;
        options.add(new Pair<>(ScreenSize.of(width, height), ScreenSize.of(pages, blanked)));
    }

    public static void warnThrowableMessage(String message, Throwable throwable, Object... values) {
        CommonMain.LOGGER.warn(new FormattedMessage(message, values, throwable));
    }

    public static MenuType<AbstractMenu> getMenuType() {
        return menuType;
    }
}
