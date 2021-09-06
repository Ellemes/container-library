package ninjaphenix.container_library.client.gui;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.internal.api.client.gui.AbstractScreen;

import java.util.Collections;
import java.util.List;

public final class SingleScreen extends AbstractScreen {
    public SingleScreen(AbstractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.initializeSlots(playerInventory);

        imageWidth = 14 + 18 * width;
        imageHeight = 17 + 97 + 18 * height;
    }

    private void initializeSlots(Inventory playerInventory) {
        for (int i = 0; i < menu.getInventory().getContainerSize(); i++) {
            int x = i % menuWidth;
            int y = (i - x) / menuWidth;
            menu.addClientSlot(new Slot(menu.getInventory(), i, x * Utils.SLOT_SIZE + 8, y * Utils.SLOT_SIZE + Utils.SLOT_SIZE));
        }
        int left = (menuWidth * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (menuHeight * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                menu.addClientSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            menu.addClientSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }
}
