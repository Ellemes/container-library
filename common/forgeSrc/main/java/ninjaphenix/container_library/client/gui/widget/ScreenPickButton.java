package ninjaphenix.container_library.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;

public final class ScreenPickButton extends Button {
    private static final ResourceLocation WARNING_TEXTURE = Utils.id("textures/gui/warning.png");
    private final ResourceLocation texture;
    private final boolean showWarningSymbol;
    private final boolean isCurrentPreference;

    public ScreenPickButton(int x, int y, int width, int height, ResourceLocation texture, Component message, boolean showWarningSymbol, boolean isCurrentPreference, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, width, height, message, onPress, onTooltip);
        this.texture = texture;
        this.showWarningSymbol = showWarningSymbol;
        this.isCurrentPreference = isCurrentPreference;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        GuiComponent.blit(stack, x, y, 0, height * (this.isHoveredOrFocused() ? 1 : isCurrentPreference ? 2 : 0), width, height, width, height * 3);
        if (showWarningSymbol) {
            RenderSystem.setShaderTexture(0, WARNING_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            GuiComponent.blit(stack, x + width - 28, y + 9, 0, 0, 16, 32, 16, 32);
        }
    }

    public void renderButtonTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (isHovered) {
            this.renderToolTip(stack, mouseX, mouseY);
        } else if (this.isFocused()) {
            this.renderToolTip(stack, x, y);
        }
    }
}
