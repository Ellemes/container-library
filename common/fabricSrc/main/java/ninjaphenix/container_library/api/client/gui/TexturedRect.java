package ninjaphenix.container_library.api.client.gui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

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
        DrawableHelper.drawTexture(stack, x, y, textureX, textureY, width, height, textureWidth, textureHeight);
    }
}
