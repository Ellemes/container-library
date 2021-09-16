package ninjaphenix.container_library.api.client.gui;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
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
import java.util.List;
import java.util.Map;

public abstract class AbstractScreen extends HandledScreen<AbstractHandler> {
    private static final Map<Identifier, ScreenConstructor<?>> SCREEN_CONSTRUCTORS = new HashMap<>();
    private static final Map<Identifier, ScreenSizeRetriever> SIZE_RETRIEVERS = new HashMap<>();
    @Debug
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
        ScreenSize screenSize = ScreenSize.current();
        int slots = handler.getInventory().size();
        // todo: expose this kind of functionality as api
        //  this should support showing screens up to what the single screen can show given the screen size can support it.
        {
            int screenSlots = screenSize.getHeight() >= 276 ? 81 : 54;
            if (slots <= screenSlots && (preference.equals(Utils.PAGE_SCREEN_TYPE) || preference.equals(Utils.SCROLL_SCREEN_TYPE))) {
                preference = Utils.SINGLE_SCREEN_TYPE;
            }
        }
        return SCREEN_CONSTRUCTORS.getOrDefault(preference, ScreenConstructor.NULL).createScreen(handler, playerInventory, title, SIZE_RETRIEVERS.get(preference).get(slots, screenSize.getWidth(), screenSize.getHeight()));
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenType(Identifier type, ScreenConstructor<?> screenConstructor) {
        SCREEN_CONSTRUCTORS.putIfAbsent(type, screenConstructor);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenSizeRetriever(Identifier type, ScreenSizeRetriever retriever) {
        SIZE_RETRIEVERS.putIfAbsent(type, retriever);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static boolean isScreenTypeDeclared(Identifier type) {
        return SCREEN_CONSTRUCTORS.containsKey(type);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
        if (AbstractScreen.DEBUG_RENDER) {
            this.renderTooltip(stack, new LiteralText("width: " + width), 5, 20);
            this.renderTooltip(stack, new LiteralText("height: " + height), 5, 40);
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
