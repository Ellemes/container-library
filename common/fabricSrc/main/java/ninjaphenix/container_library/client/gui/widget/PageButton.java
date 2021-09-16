package ninjaphenix.container_library.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.Utils;

public final class PageButton extends ButtonWidget {
    private static final Identifier TEXTURE = Utils.resloc("textures/gui/page_buttons.png");
    private final int textureOffset;

    public PageButton(int x, int y, int textureOffset, Text message, PressAction onPress, TooltipSupplier onTooltip) {
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
        RenderSystem.setShaderTexture(0, PageButton.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        DrawableHelper.drawTexture(stack, x, y, textureOffset * 12, this.getYImage(this.isHovered()) * 12, width, height, 32, 48);
    }

    public void renderButtonTooltip(MatrixStack stack, int mouseX, int mouseY) {
        if (active) {
            if (hovered) {
                this.renderTooltip(stack, mouseX, mouseY);
            } else if (this.isFocused()) {
                this.renderTooltip(stack, x, y);
            }
        }
    }
}
