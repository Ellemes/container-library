package ninjaphenix.container_library.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.ScreenConstructor;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.function.ScreenSizeRetriever;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractScreen extends AbstractContainerScreen<AbstractMenu> {
    private static final Map<ResourceLocation, ScreenConstructor<?>> SCREEN_CONSTRUCTORS = new HashMap<>();
    private static final Map<ResourceLocation, ScreenSizeRetriever> SIZE_RETRIEVERS = new HashMap<>();
    @VisibleForDebug
    public static boolean DEBUG_RENDER = false;

    protected final int menuWidth, menuHeight, totalSlots;

    protected AbstractScreen(AbstractMenu menu, Inventory playerInventory, Component title, ScreenSize screenSize) {
        super(menu, playerInventory, title);
        totalSlots = menu.getInventory().getContainerSize();
        menuWidth = screenSize.getWidth();
        menuHeight = screenSize.getHeight();
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static AbstractScreen createScreen(AbstractMenu menu, Inventory inventory, Component title) {
        ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
        ScreenSize screenSize = ScreenSize.current();
        int slots = menu.getInventory().getContainerSize();
        // todo: expose this kind of functionality as api, also less hard-code it
        {
            int screenSlots = screenSize.getHeight() >= 276 ? 81 : 54;
            if (slots <= screenSlots && (preference.equals(Utils.PAGE_SCREEN_TYPE) || preference.equals(Utils.SCROLL_SCREEN_TYPE))) {
                preference = Utils.SINGLE_SCREEN_TYPE;
            }
        }
        return SCREEN_CONSTRUCTORS.getOrDefault(preference, ScreenConstructor.NULL).createScreen(menu, inventory, title, SIZE_RETRIEVERS.get(preference).get(slots, screenSize.getWidth(), screenSize.getHeight()));
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenType(ResourceLocation type, ScreenConstructor<?> screenConstructor) {
        SCREEN_CONSTRUCTORS.putIfAbsent(type, screenConstructor);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareScreenSizeRetriever(ResourceLocation type, ScreenSizeRetriever retriever) {
        SIZE_RETRIEVERS.putIfAbsent(type, retriever);
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static boolean isScreenTypeDeclared(ResourceLocation type) {
        return SCREEN_CONSTRUCTORS.containsKey(type);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
        if (AbstractScreen.DEBUG_RENDER) {
            this.renderTooltip(stack, new TextComponent("width: " + width), 5, 20);
            this.renderTooltip(stack, new TextComponent("height: " + height), 5, 40);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        } else if (PlatformUtils.isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            minecraft.setScreen(new PickScreen(() -> {
                menu.clearSlots(); // Clear slots as each screen position slots differently.
                return AbstractScreen.createScreen(menu, minecraft.player.getInventory(), title);
            }, null));
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

    public abstract List<Rect2i> getExclusionZones();
}
