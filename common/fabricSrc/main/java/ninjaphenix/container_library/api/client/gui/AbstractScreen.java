package ninjaphenix.container_library.api.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
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
    public static boolean DEBUG_RENDER = false;

    protected final int menuWidth, menuHeight, totalSlots;

    protected AbstractScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title, ScreenSize screenSize) {
        super(handler, playerInventory, title);
        totalSlots = handler.getInventory().size();
        menuWidth = screenSize.getWidth();
        menuHeight = screenSize.getHeight();
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static AbstractScreen createScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title) {
        Identifier preference = ConfigWrapper.getInstance().getPreferredScreenType();
        int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int slots = handler.getInventory().size();

        if (AbstractScreen.canSingleScreenDisplay(slots, scaledWidth, scaledHeight) && AbstractScreen.shouldPreferSingleScreen(preference)) {
            preference = Utils.SINGLE_SCREEN_TYPE;
        }

        return AbstractScreen.SCREEN_CONSTRUCTORS.getOrDefault(preference, ScreenConstructor.NULL).createScreen(handler, playerInventory, title, AbstractScreen.SIZE_RETRIEVERS.get(preference).get(slots, scaledWidth, scaledHeight));
    }

    private static boolean shouldPreferSingleScreen(Identifier preference) {
        return AbstractScreen.PREFERS_SINGLE_SCREEN.contains(preference);
    }

    private static boolean canSingleScreenDisplay(int slots, int width, int height) {
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

    public static void setPrefersSingleScreen(Identifier type) {
        AbstractScreen.PREFERS_SINGLE_SCREEN.add(type);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
        if (AbstractScreen.DEBUG_RENDER && client.options.debugEnabled) {
            this.renderTooltip(stack, new LiteralText("w: " + width + ", h: " + height), 5, 20);
            this.renderTooltip(stack, new LiteralText("x: " + mouseX + ", y: " + mouseY), 5, 40);
            this.renderTooltip(stack, new LiteralText("bW: " + backgroundWidth + ", bH: " + backgroundHeight), 5, 60);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if (PlatformUtils.isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            client.setScreen(new PickScreen(() -> {
                handler.clearSlots(); // Clear slots as each screen position slots differently.
                return AbstractScreen.createScreen(handler, client.player.getInventory(), title);
            }, null));
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

    public abstract List<Rect2i> getExclusionZones();
}
