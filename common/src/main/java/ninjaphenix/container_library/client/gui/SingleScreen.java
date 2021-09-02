package ninjaphenix.container_library.client.gui;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.internal.api.client.gui.AbstractScreen;

import java.util.Collections;
import java.util.List;

public final class SingleScreen extends AbstractScreen {
    public SingleScreen(AbstractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 14 + 18 * width;
        imageHeight = 17 + 97 + 18 * height;
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }
}
