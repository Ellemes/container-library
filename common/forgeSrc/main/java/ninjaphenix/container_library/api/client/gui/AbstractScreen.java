package ninjaphenix.container_library.api.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.ScreenConstructor;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;

public abstract class AbstractScreen extends ContainerScreen<AbstractHandler> {
    private static final Map<ResourceLocation, ScreenConstructor<?>> SCREEN_CONSTRUCTORS = new HashMap<>();
    private static final Map<ResourceLocation, ScreenSizeRetriever> SIZE_RETRIEVERS = new HashMap<>();
    private static final Set<ResourceLocation> PREFERS_SINGLE_SCREEN = new HashSet<>();
    public static boolean DEBUG_RENDER = false;

    protected final int inventoryWidth, inventoryHeight, totalSlots;

    protected AbstractScreen(AbstractHandler handler, PlayerInventory playerInventory, ITextComponent title, ScreenSize screenSize) {
        super(handler, playerInventory, title);
        totalSlots = handler.getInventory().getContainerSize();
        inventoryWidth = screenSize.getWidth();
        inventoryHeight = screenSize.getHeight();
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static AbstractScreen createScreen(AbstractHandler handler, PlayerInventory playerInventory, ITextComponent title) {
        ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int slots = handler.getInventory().getContainerSize();

        if (AbstractScreen.canSingleScreenDisplay(slots, scaledWidth, scaledHeight) && AbstractScreen.shouldPreferSingleScreen(preference)) {
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

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        AbstractScreen.SCREEN_CONSTRUCTORS.putIfAbsent(type, screenConstructor);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenSizeRetriever(ResourceLocation type, ScreenSizeRetriever retriever) {
        AbstractScreen.SIZE_RETRIEVERS.putIfAbsent(type, retriever);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
        //noinspection ConstantConditions
        if (AbstractScreen.DEBUG_RENDER && minecraft.options.renderDebug) {
            this.renderTooltip(stack, new StringTextComponent("w: " + width + ", h: " + height), 5, 20);
            this.renderTooltip(stack, new StringTextComponent("x: " + mouseX + ", y: " + mouseY), 5, 40);
            this.renderTooltip(stack, new StringTextComponent("bW: " + imageWidth + ", bH: " + imageHeight), 5, 60);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if (PlatformUtils.isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            minecraft.setScreen(new PickScreen(() -> AbstractScreen.createScreen(menu, minecraft.player.inventory, title), menu));
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
    public abstract List<Rectangle2d> getExclusionZones();
}
