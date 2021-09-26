package ninjaphenix.container_library.api.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public final class TexturedRect {
    private final int x, y, width, height, textureX, textureY, textureWidth, textureHeight;

    public TexturedRect(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureX = textureX;
        this.textureY = textureY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void render(MatrixStack stack) {
        AbstractGui.blit(stack, x, y, textureX, textureY, width, height, textureWidth, textureHeight);
    }
}
