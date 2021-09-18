package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SingleScreen extends AbstractScreen {
    private final Set<TexturedRect> blankArea = new HashSet<>();
    private final Identifier textureLocation;
    private final int textureWidth, textureHeight, blankSlots;

    public SingleScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title, ScreenSize screenSize) {
        super(handler, playerInventory, title, screenSize);

        this.initializeSlots(playerInventory);

        textureLocation = new Identifier("ninjaphenix_container_lib", "textures/gui/container/shared_" + menuWidth + "_" + menuHeight + ".png");
        textureWidth = switch (menuWidth) {
            case 9 -> menuHeight == 3 ? 176 : 208;
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

        blankSlots = (menuWidth * menuHeight) - totalSlots;

        backgroundWidth = Utils.CONTAINER_PADDING_LDR + Utils.SLOT_SIZE * menuWidth + Utils.CONTAINER_PADDING_LDR;
        backgroundHeight = Utils.CONTAINER_HEADER_HEIGHT + Utils.SLOT_SIZE * menuHeight + 14 + Utils.SLOT_SIZE * 3 + 4 + Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR;
    }

    @Override
    protected void init() {
        super.init();
        if (blankSlots > 0) {
            blankArea.clear();
            int rows = MathHelper.floorDiv(blankSlots, menuWidth);
            int remainder = (blankSlots - menuWidth * rows);
            int yTop = y + Utils.CONTAINER_HEADER_HEIGHT + (menuHeight - 1) * Utils.SLOT_SIZE;
            int xLeft = x + Utils.CONTAINER_PADDING_LDR;
            for (int i = 0; i < rows; i++) {
                blankArea.add(new TexturedRect(xLeft, yTop, menuWidth * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                        Utils.CONTAINER_PADDING_LDR, backgroundHeight, textureWidth, textureHeight));
                yTop -= Utils.SLOT_SIZE;
            }
            if (remainder > 0) {
                int xRight = x + Utils.CONTAINER_PADDING_LDR + menuWidth * Utils.SLOT_SIZE;
                int width = remainder * Utils.SLOT_SIZE;
                blankArea.add(new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE,
                        Utils.CONTAINER_PADDING_LDR, backgroundHeight, textureWidth, textureHeight));
            }
        }
    }

    @Override
    protected void drawBackground(MatrixStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(stack, x, y, 0, 0, backgroundWidth, backgroundHeight, textureWidth, textureHeight);
        blankArea.forEach(image -> image.render(stack));
    }

    @Override
    protected void drawForeground(MatrixStack stack, int mouseX, int mouseY) {
        textRenderer.draw(stack, title, 8, 6, 0x404040);
        textRenderer.draw(stack, playerInventoryTitle, 8, backgroundHeight - 96 + 2, 0x404040);
    }

    private void initializeSlots(PlayerInventory playerInventory) {
        for (int i = 0; i < handler.getInventory().size(); i++) {
            int x = i % menuWidth;
            int y = (i - x) / menuWidth;
            handler.addClientSlot(new Slot(handler.getInventory(), i, x * Utils.SLOT_SIZE + 8, y * Utils.SLOT_SIZE + Utils.SLOT_SIZE));
        }
        int left = (menuWidth * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (menuHeight * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                handler.addClientSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            handler.addClientSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }

    public static ScreenSize retrieveScreenSize(int slots, int scaledWidth, int scaledHeight) {
        int width;

        if (slots <= 81) {
            width = 9;
        } else if (slots <= 108) {
            width = 12;
        } else if (slots <= 135) {
            width = 15;
        } else if (slots <= 270) {
            width = 18;
        } else {
            return null;
        }

        int height;

        if (slots <= 27) {
            height = 3;
        } else if (slots <= 54) {
            height = 6;
        } else if (slots <= 162) {
            height = 9;
        } else if (slots <= 216) {
            height = 12;
        } else /* if (slots <= 270) */ {
            height = 15;
        } // slots is guaranteed to be 270 or below when getting width.

        return ScreenSize.of(width, height);
    }
}
