package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.internal.api.client.gui.AbstractScreen;

import java.util.Collections;
import java.util.List;

public final class SingleScreen extends AbstractScreen {
    private final ResourceLocation textureLocation;
    private final int textureWidth;
    private final int textureHeight;

    public SingleScreen(AbstractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.initializeSlots(playerInventory);

        textureLocation = new ResourceLocation("ninjaphenix_container_lib", "textures/gui/container/shared_"+menuWidth+"_"+menuHeight+".png");
        textureWidth = switch (menuWidth) {
            case 9 -> 208;
            case 12 -> 256;
            case 15 -> 320;
            case 18 -> 368;
            default -> throw new IllegalStateException("Unexpected value: " + menuWidth);
        };
        textureHeight = switch (menuHeight) {
            case 3 -> 192;
            case 6 -> 240;
            case 9 -> 304;
            case 12 -> 352;
            case 15 -> 416;
            default -> throw new IllegalStateException("Unexpected value: " + menuHeight);
        };

        imageWidth = 14 + 18 * menuWidth;
        imageHeight = 17 + 97 + 18 * menuHeight;
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);
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
