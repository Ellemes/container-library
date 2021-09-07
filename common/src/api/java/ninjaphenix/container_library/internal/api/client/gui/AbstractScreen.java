package ninjaphenix.container_library.internal.api.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.internal.api.inventory.AbstractMenu;
import ninjaphenix.container_library.internal.api.inventory.screen.ScreenMeta;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Function;

@ApiStatus.Experimental
public abstract class AbstractScreen<T extends AbstractMenu<R>, R extends ScreenMeta> extends AbstractContainerScreen<T> {
    protected final R screenMeta;
    private final Integer inventoryLabelLeft;

    protected AbstractScreen(T container, Inventory playerInventory, Component title, Function<R, Integer> inventoryLabelLeftFunction) {
        super(container, playerInventory, title);
        screenMeta = container.screenMeta;
        inventoryLabelLeft = inventoryLabelLeftFunction.apply(screenMeta);
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, screenMeta.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 4210752);
        font.draw(stack, playerInventoryTitle, inventoryLabelLeft, imageHeight - 96 + 2, 4210752);
    }

    /**
     * Implementors of this method should call super to allow pick screen key bind to work.
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (PlatformUtils.getInstance().isConfigKeyPressed(keyCode, scanCode, modifiers)) {
            minecraft.setScreen(new PickScreen(NetworkWrapper.getInstance().getScreenOptions(), null, (selection) -> NetworkWrapper.getInstance().c_openInventoryAt(menu.pos, selection)));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected final void renderButtonTooltip(AbstractButton button, PoseStack stack, int x, int y) {
        this.renderTooltip(stack, button.getMessage(), x, y);
    }

    public abstract List<Rect2i> getExclusionZones();
}
