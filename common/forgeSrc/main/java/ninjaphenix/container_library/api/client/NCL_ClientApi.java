package ninjaphenix.container_library.api.client;

import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(texture, "texture must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(warningTest, "warningTest must not be null");
        Objects.requireNonNull(warningText, "warningText must not be null");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, warningTest, warningText);
    }

    /**
     * Register button for screen type pick screen
     * Note: texture must be 96 x 288 ( 3 images: normal, hovered, current )
     */
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(texture, "texture must not be null");
        Objects.requireNonNull(title, "title must not be null");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, ScreenSizePredicate::noTest, List.of());
    }

    /**
     * Register screen constructor.
     */
    public static void registerScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(screenConstructor, "screenConstructor must not be null");
        //noinspection deprecation
        AbstractScreen.declareScreenType(type, screenConstructor);
    }

    /**
     * Register default screen sizes, it is planned to allow players to override the default screen sizes in the future.
     */
    public static void registerDefaultScreenSize(ResourceLocation type, ScreenSizeRetriever retriever) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(retriever, "retriever must not be null");
        //noinspection deprecation
        AbstractScreen.declareScreenSizeRetriever(type, retriever);
    }

    /**
     * Uses the signle screen type over the specified type if the single screen will visually fit in the game window.
     * Note: May be renamed in the future.
     */
    public static void setPrefersSingleScreen(ResourceLocation type) {
        Objects.requireNonNull(type, "type must not be null");
        AbstractScreen.setPrefersSingleScreen(type);
    }
}
