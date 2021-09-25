package ninjaphenix.container_library.api.client;

import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.v2.client.NCL_ClientApiV2;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * @deprecated Use V2 instead {@link ninjaphenix.container_library.api.v2.client.NCL_ClientApiV2 }
 */
@Deprecated
public final class NCL_ClientApi {
    private NCL_ClientApi() {
        throw new IllegalStateException("NCL_ClientApi cannot be instantiated.");
    }

    /**
     * Call on client side to attempt to open an inventory.
     */
    public static void openInventoryAt(BlockPos pos) {
        Objects.requireNonNull(pos, "pos must not be null");
        NetworkWrapper.getInstance().c_openInventoryAt(pos);
    }

    /**
     * Register button for screen type pick screen with an optional error message.
     * Note: texture must be 96 x 288 ( 3 images: normal, hovered, current )
     */
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title, ScreenSizePredicate warningTest, List<Component> warningText) {
        NCL_ClientApiV2.registerScreenButton(type, texture, title, warningTest, warningText);
    }

    /**
     * Register button for screen type pick screen
     * Note: texture must be 96 x 288 ( 3 images: normal, hovered, current )
     */
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title) {
        NCL_ClientApiV2.registerScreenButton(type, texture, title);
    }

    /**
     * Register screen constructor.
     */
    public static void registerScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        NCL_ClientApiV2.registerScreenType(type, screenConstructor);
    }

    /**
     * Register default screen sizes, it is planned to allow players to override the default screen sizes in the future.
     */
    public static void registerDefaultScreenSize(ResourceLocation type, ScreenSizeRetriever retriever) {
        NCL_ClientApiV2.registerDefaultScreenSize(type, retriever);
    }

    /**
     * Uses the single screen type over the specified type if the single screen will visually fit in the game window.
     * Note: May be renamed in the future.
     */
    public static void setPrefersSingleScreen(ResourceLocation type) {
        Objects.requireNonNull(type, "type must not be null");
        AbstractScreen.setPrefersSingleScreen(type);
    }
}
