package ninjaphenix.container_library.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ninjaphenix.container_library.Utils;

public final class PageButton extends Button {
    private static final ResourceLocation TEXTURE = Utils.id("textures/gui/page_buttons.png");
    private final int textureOffset;

    public PageButton(int x, int y, int textureOffset, ITextComponent message, IPressable onPress, ITooltip onTooltip) {
        super(x, y, 12, 12, message, onPress, onTooltip);
        this.textureOffset = textureOffset;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.setFocused(false);
        }
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Minecraft.getInstance().getTextureManager().bind(PageButton.TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        AbstractGui.blit(stack, x, y, textureOffset * 12, this.getYImage(this.isHovered()) * 12, width, height, 32, 48);
    }

    public void renderButtonTooltip(MatrixStack stack, int mouseX, int mouseY) {
        if (active) {
            if (isHovered) {
                this.renderToolTip(stack, mouseX, mouseY);
            } else if (this.isFocused()) {
                this.renderToolTip(stack, x, y);
            }
        }
    }
}
