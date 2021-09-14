package ninjaphenix.container_library.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;

public final class ScreenPickButton extends Button {
    private static final ResourceLocation WARNING_TEXTURE = Utils.resloc("textures/gui/warning.png");
    private final ResourceLocation texture;
    private final boolean warn;
    private final boolean isCurrent;

    public ScreenPickButton(int x, int y, int width, int height, ResourceLocation texture, Component message, boolean warn, boolean isCurrent, OnPress pressAction, OnTooltip tooltipRenderer) {
        super(x, y, width, height, message, pressAction, tooltipRenderer);
        this.texture = texture;
        this.warn = warn;
        this.isCurrent = isCurrent;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        GuiComponent.blit(stack, x, y, 0, height * (this.isHovered() ? 1 : isCurrent ? 2 : 0), width, height, width, height * 3);
        if (warn) {
            RenderSystem.setShaderTexture(0, WARNING_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            GuiComponent.blit(stack, x + width - 28, y + 9, 0, 0, 16, 32, 16, 32);
        }
    }

    public void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (isHovered) {
            this.renderToolTip(stack, mouseX, mouseY);
        } else if (this.isFocused()) {
            this.renderToolTip(stack, x, y);
        }
    }
}
