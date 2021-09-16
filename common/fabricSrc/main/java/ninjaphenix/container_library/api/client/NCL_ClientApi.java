package ninjaphenix.container_library.api.client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

import java.util.List;
import java.util.Objects;

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

    // may be wise to allow width and height to be edited rather than fixed 256x256
    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title, ScreenSizePredicate warningTest, List<Component> warningText) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(texture, "texture must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(warningTest, "warningTest must not be null");
        Objects.requireNonNull(warningText, "warningText must not be null");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, warningTest, warningText);
    }

    public static void registerScreenButton(ResourceLocation type, ResourceLocation texture, Component title) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(texture, "texture must not be null");
        Objects.requireNonNull(title, "title must not be null");
        //noinspection deprecation
        PickScreen.declareButtonSettings(type, texture, title, ScreenSizePredicate::noTest, List.of());
    }

    public static void registerScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(screenConstructor, "screenConstructor must not be null");
        //noinspection deprecation
        if (screenConstructor == ScreenConstructor.NULL) {
            throw new IllegalArgumentException("screenConstructor must not be ScreenConstructor.NULL");
        }
        //noinspection deprecation
        AbstractScreen.declareScreenType(type, screenConstructor);
    }

    // Register default screen sizes, currently hard-coded however it is planned to allow users to override this in the future.
    public static void registerDefaultScreenSize(ResourceLocation type, ScreenSizeRetriever retriever) {
        //noinspection deprecation
        AbstractScreen.declareScreenSizeRetriever(type, retriever);
    }
}
