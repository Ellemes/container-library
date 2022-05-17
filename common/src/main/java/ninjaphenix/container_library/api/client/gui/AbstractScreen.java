package ninjaphenix.container_library.api.client.gui;

import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ninjaphenix.container_library.api.client.ScreenConstructor;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.client.gui.PickScreen;
import ellemes.container_library.wrappers.PlatformUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractScreen extends AbstractContainerScreen<AbstractHandler> {
    private static final Map<ResourceLocation, ScreenConstructor<?>> SCREEN_CONSTRUCTORS = new HashMap<>();
    private static final Map<ResourceLocation, ScreenSizeRetriever> SIZE_RETRIEVERS = new HashMap<>();
    private static final Set<ResourceLocation> PREFERS_SINGLE_SCREEN = new HashSet<>();

    protected final int inventoryWidth, inventoryHeight, totalSlots;

    protected AbstractScreen(AbstractHandler handler, Inventory playerInventory, Component title, ScreenSize screenSize) {
        super(handler, playerInventory, title);
        totalSlots = handler.getInventory().getContainerSize();
        inventoryWidth = screenSize.getWidth();
        inventoryHeight = screenSize.getHeight();
    }

    @ApiStatus.Internal
    public static AbstractScreen createScreen(AbstractHandler handler, Inventory playerInventory, Component title) {
        ResourceLocation forcedScreenType = handler.getForcedScreenType();
        ResourceLocation preference = forcedScreenType != null ? forcedScreenType : CommonMain.getConfigWrapper().getPreferredScreenType();
        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int slots = handler.getInventory().getContainerSize();

        if (forcedScreenType == null && AbstractScreen.canSingleScreenDisplay(slots, scaledWidth, scaledHeight) && AbstractScreen.shouldPreferSingleScreen(preference)) {
            preference = Utils.SINGLE_SCREEN_TYPE;
        }

        ScreenSize screenSize = AbstractScreen.SIZE_RETRIEVERS.get(preference).get(slots, scaledWidth, scaledHeight);
        if (screenSize == null) {
            throw new IllegalStateException("screenSize should never be null...");
        }
        return AbstractScreen.SCREEN_CONSTRUCTORS.get(preference).createScreen(handler, playerInventory, title, screenSize);
    }

    private static boolean shouldPreferSingleScreen(ResourceLocation type) {
        return AbstractScreen.PREFERS_SINGLE_SCREEN.contains(type);
    }

    private static boolean canSingleScreenDisplay(int slots, int scaledWidth, int scaledHeight) {
        if (slots <= 54) {
            return true;
        }
        if (scaledHeight >= 276) {
            if (slots <= 81) {
                return true;
            }
            if (scaledWidth >= 230 && slots <= 108) {
                return true;
            }
            if (scaledWidth >= 284 && slots <= 135) {
                return true;
            }
            if (scaledWidth >= 338 && slots <= 162) {
                return true;
            }
        }
        if (scaledWidth >= 338) {
            if (scaledHeight >= 330 && slots <= 216) {
                return true;
            }
            if (scaledHeight >= 384 && slots <= 270) {
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public static void declareScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        AbstractScreen.SCREEN_CONSTRUCTORS.putIfAbsent(type, screenConstructor);
    }

    @ApiStatus.Internal
    public static void declareScreenSizeRetriever(ResourceLocation type, ScreenSizeRetriever retriever) {
        AbstractScreen.SIZE_RETRIEVERS.putIfAbsent(type, retriever);
    }

    @ApiStatus.Internal
    public static boolean isScreenTypeDeclared(ResourceLocation type) {
        return AbstractScreen.SCREEN_CONSTRUCTORS.containsKey(type);
    }

    @ApiStatus.Internal
    public static void setPrefersSingleScreen(ResourceLocation type) {
        AbstractScreen.PREFERS_SINGLE_SCREEN.add(type);
    }

    @Nullable
    @ApiStatus.Internal
    public static ScreenSize getScreenSize(ResourceLocation type, int slots, int scaledWidth, int scaledHeight) {
        return AbstractScreen.SIZE_RETRIEVERS.get(type).get(slots, scaledWidth, scaledHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if (PlatformUtils.isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            minecraft.setScreen(new PickScreen(() -> AbstractScreen.createScreen(menu, minecraft.player.getInventory(), title), menu));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * @return true if a screen specific keybinding is pressed otherwise false to follow through with additional checks.
     */
    protected boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @NotNull
    @ApiStatus.Internal
    public abstract List<Rect2i> getExclusionZones();
}
