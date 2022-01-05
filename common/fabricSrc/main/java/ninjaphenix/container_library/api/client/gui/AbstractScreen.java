package ninjaphenix.container_library.api.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

public abstract class AbstractScreen extends HandledScreen<AbstractHandler> {
    private static final Map<Identifier, ScreenConstructor<?>> SCREEN_CONSTRUCTORS = new HashMap<>();
    private static final Map<Identifier, ScreenSizeRetriever> SIZE_RETRIEVERS = new HashMap<>();
    private static final Set<Identifier> PREFERS_SINGLE_SCREEN = new HashSet<>();

    protected final int inventoryWidth, inventoryHeight, totalSlots;

    protected AbstractScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title, ScreenSize screenSize) {
        super(handler, playerInventory, title);
        totalSlots = handler.getInventory().size();
        inventoryWidth = screenSize.getWidth();
        inventoryHeight = screenSize.getHeight();
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static AbstractScreen createScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title) {
        Identifier forcedScreenType = handler.getForcedScreenType();
        Identifier preference = forcedScreenType != null ? forcedScreenType : ConfigWrapper.getInstance().getPreferredScreenType();
        int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int slots = handler.getInventory().size();

        if (forcedScreenType == null && AbstractScreen.canSingleScreenDisplay(slots, scaledWidth, scaledHeight) && AbstractScreen.shouldPreferSingleScreen(preference)) {
            preference = Utils.SINGLE_SCREEN_TYPE;
        }

        ScreenSize screenSize = AbstractScreen.SIZE_RETRIEVERS.get(preference).get(slots, scaledWidth, scaledHeight);
        if (screenSize == null) {
            throw new IllegalStateException("screenSize should never be null...");
        }
        return AbstractScreen.SCREEN_CONSTRUCTORS.get(preference).createScreen(handler, playerInventory, title, screenSize);
    }

    private static boolean shouldPreferSingleScreen(Identifier type) {
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
    public static void declareScreenType(Identifier type, ScreenConstructor<?> screenConstructor) {
        AbstractScreen.SCREEN_CONSTRUCTORS.putIfAbsent(type, screenConstructor);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenSizeRetriever(Identifier type, ScreenSizeRetriever retriever) {
        AbstractScreen.SIZE_RETRIEVERS.putIfAbsent(type, retriever);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static boolean isScreenTypeDeclared(Identifier type) {
        return AbstractScreen.SCREEN_CONSTRUCTORS.containsKey(type);
    }

    @ApiStatus.Internal
    public static void setPrefersSingleScreen(Identifier type) {
        AbstractScreen.PREFERS_SINGLE_SCREEN.add(type);
    }

    @Nullable
    @ApiStatus.Internal
    public static ScreenSize getScreenSize(Identifier type, int slots, int scaledWidth, int scaledHeight) {
        return AbstractScreen.SIZE_RETRIEVERS.get(type).get(slots, scaledWidth, scaledHeight);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if (PlatformUtils.isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            client.setScreen(new PickScreen(() -> AbstractScreen.createScreen(handler, client.player.getInventory(), title), handler));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || client.options.keyInventory.matchesKey(keyCode, scanCode)) {
            client.player.closeHandledScreen();
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
    public abstract List<Rect2i> getExclusionZones();
}
