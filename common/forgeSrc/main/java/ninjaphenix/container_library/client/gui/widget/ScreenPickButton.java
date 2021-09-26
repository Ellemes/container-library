package ninjaphenix.container_library.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ninjaphenix.container_library.Utils;

public final class ScreenPickButton extends Button {
    private static final ResourceLocation WARNING_TEXTURE = Utils.id("textures/gui/warning.png");
    private final ResourceLocation texture;
    private final boolean showWarningSymbol;
    private final boolean isCurrentPreference;

    public ScreenPickButton(int x, int y, int width, int height, ResourceLocation texture, ITextComponent message, boolean showWarningSymbol, boolean isCurrentPreference, IPressable onPress, ITooltip onTooltip) {
        super(x, y, width, height, message, onPress, onTooltip);
        this.texture = texture;
        this.showWarningSymbol = showWarningSymbol;
        this.isCurrentPreference = isCurrentPreference;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Minecraft.getInstance().getTextureManager().bind(texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        AbstractGui.blit(stack, x, y, 0, height * (this.isHovered() ? 1 : isCurrentPreference ? 2 : 0), width, height, width, height * 3);
        if (showWarningSymbol) {
            Minecraft.getInstance().getTextureManager().bind(WARNING_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            AbstractGui.blit(stack, x + width - 28, y + 9, 0, 0, 16, 32, 16, 32);
        }
    }

    public void renderButtonTooltip(MatrixStack stack, int mouseX, int mouseY) {
        if (isHovered) {
            this.renderToolTip(stack, mouseX, mouseY);
        } else if (this.isFocused()) {
            this.renderToolTip(stack, x, y);
        }
    }
}
