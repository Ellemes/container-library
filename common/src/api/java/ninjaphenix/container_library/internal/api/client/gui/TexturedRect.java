package ninjaphenix.container_library.internal.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public record TexturedRect(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
    public void render(PoseStack stack) {
        GuiComponent.blit(stack, x, y, textureX, textureY, width, height, textureWidth, textureHeight);
    }
}
