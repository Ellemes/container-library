package ellemes.container_library.api.v3.client;

import ellemes.container_library.Utils;
import ellemes.container_library.api.client.ScreenConstructor;
import ellemes.container_library.api.client.function.ScreenSizePredicate;
import ellemes.container_library.api.client.function.ScreenSizeRetriever;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.client.gui.PickScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ScreenTypeApi {
    private ScreenTypeApi() {
        throw new IllegalStateException("ScreenTypeApi should not be instantiated.");
    }

    /**
     * Register button for screen type pick screen with an optional error message.
     * Note: texture must be 96 x 288 ( 3 images: normal, hovered, current )
     */
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title, ScreenSizePredicate warningTest, List<Component> warningText) {
        Utils.requiresNonNull(type, "type");
        Utils.requiresNonNull(texture, "texture");
        Utils.requiresNonNull(title, "title");
        Utils.requiresNonNull(warningTest, "warningTest");
        Utils.requiresNonNull(warningText, "warningText");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, warningTest, warningText);
    }

    /**
     * Register button for screen type pick screen
     * Note: texture must be 96 x 288 ( 3 images: normal, hovered, current )
     */
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title) {
        Utils.requiresNonNull(type, "type");
        Utils.requiresNonNull(texture, "texture");
        Utils.requiresNonNull(title, "title");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, ScreenSizePredicate::noTest, List.of());
    }

    /**
     * Register screen constructor.
     */
    public static void registerScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        Utils.requiresNonNull(type, "type");
        Utils.requiresNonNull(screenConstructor, "screenConstructor");
        AbstractScreen.declareScreenType(type, screenConstructor);
    }

    /**
     * Register default screen sizes, it is planned to allow players to override the default screen sizes in the future.
     */
    public static void registerDefaultScreenSize(ResourceLocation type, ScreenSizeRetriever retriever) {
        Utils.requiresNonNull(type, "type");
        Utils.requiresNonNull(retriever, "retriever");
        AbstractScreen.declareScreenSizeRetriever(type, retriever);
    }

    /**
     * Uses the single screen type over the specified type if the single screen will visually fit in the game window.
     * Note: May be renamed in the future.
     */
    public static void setPrefersSingleScreen(ResourceLocation type) {
        Utils.requiresNonNull(type, "type");
        AbstractScreen.setPrefersSingleScreen(type);
    }
}
