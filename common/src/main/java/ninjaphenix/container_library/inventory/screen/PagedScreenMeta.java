package ninjaphenix.container_library.inventory.screen;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.internal.api.inventory.screen.ScreenMeta;

public final class PagedScreenMeta extends ScreenMeta {
    public final int blankSlots, pages;

    public PagedScreenMeta(int width, int height, int pages, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        this.pages = pages;
        blankSlots = pages * width * height - totalSlots;
    }
}
