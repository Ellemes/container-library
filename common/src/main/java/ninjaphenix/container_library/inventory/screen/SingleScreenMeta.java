package ninjaphenix.container_library.inventory.screen;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.internal.api.inventory.screen.ScreenMeta;

public final class SingleScreenMeta extends ScreenMeta {
    public final int blankSlots;

    public SingleScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        blankSlots = width * height - totalSlots;
    }
}
