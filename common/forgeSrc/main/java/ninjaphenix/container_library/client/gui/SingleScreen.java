package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SingleScreen extends AbstractScreen {
    private final Set<TexturedRect> blankArea = new HashSet<>();
    private final ResourceLocation textureLocation;
    private final int textureWidth, textureHeight, blankSlots;

    public SingleScreen(AbstractHandler handler, PlayerInventory playerInventory, ITextComponent title, ScreenSize screenSize) {
        super(handler, playerInventory, title, screenSize);

        this.initializeSlots(playerInventory);

        textureLocation = new ResourceLocation("ninjaphenix_container_lib", "textures/gui/container/shared_" + inventoryWidth + "_" + inventoryHeight + ".png");

        if (inventoryWidth == 9) {
            textureWidth = inventoryHeight == 3 ? 176 : 208;
        } else if (inventoryWidth == 12) {
            textureWidth = 256;
        } else if (inventoryWidth == 15) {
            textureWidth = 320;
        } else if (inventoryWidth == 18) {
            textureWidth = 368;
        } else {
            throw new IllegalStateException("Unexpected value: " + inventoryWidth);
        }

        if (inventoryHeight == 3) {
            textureHeight = 192;
        } else if (inventoryHeight == 6) {
            textureHeight = 240;
        } else if (inventoryHeight == 9) {
            textureHeight = 304;
        } else if (inventoryHeight == 12) {
            textureHeight = 352;
        } else if (inventoryHeight == 15) {
            textureHeight = 416;
        } else {
            throw new IllegalStateException("Unexpected value: " + inventoryHeight);
        }

        blankSlots = (inventoryWidth * inventoryHeight) - totalSlots;

        imageWidth = Utils.CONTAINER_PADDING_LDR + Utils.SLOT_SIZE * inventoryWidth + Utils.CONTAINER_PADDING_LDR;
        imageHeight = Utils.CONTAINER_HEADER_HEIGHT + Utils.SLOT_SIZE * inventoryHeight + 14 + Utils.SLOT_SIZE * 3 + 4 + Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR;
    }

    @Override
    protected void init() {
        super.init();
        if (blankSlots > 0) {
            blankArea.clear();
            int rows = MathHelper.intFloorDiv(blankSlots, inventoryWidth);
            int remainder = (blankSlots - inventoryWidth * rows);
            int yTop = topPos + Utils.CONTAINER_HEADER_HEIGHT + (inventoryHeight - 1) * Utils.SLOT_SIZE;
            int xLeft = leftPos + Utils.CONTAINER_PADDING_LDR;
            for (int i = 0; i < rows; i++) {
                blankArea.add(new TexturedRect(xLeft, yTop, inventoryWidth * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                        Utils.CONTAINER_PADDING_LDR, imageHeight, textureWidth, textureHeight));
                yTop -= Utils.SLOT_SIZE;
            }
            if (remainder > 0) {
                int xRight = leftPos + Utils.CONTAINER_PADDING_LDR + inventoryWidth * Utils.SLOT_SIZE;
                int width = remainder * Utils.SLOT_SIZE;
                blankArea.add(new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE,
                        Utils.CONTAINER_PADDING_LDR, imageHeight, textureWidth, textureHeight));
            }
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        if (inventoryWidth > 9 && mouseY >= top + Utils.CONTAINER_HEADER_HEIGHT + inventoryHeight * Utils.SLOT_SIZE + Utils.CONTAINER_HEADER_HEIGHT) {
            int outsideRegion = (imageWidth - (Utils.CONTAINER_PADDING_LDR + 9 * Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR)) / 2;
            if (mouseX < left + outsideRegion || mouseX > left + imageWidth - outsideRegion) {
                return true;
            }
        }
        return super.hasClickedOutside(mouseX, mouseY, left, top, button);
    }

    @Override
    protected void renderBg(MatrixStack stack, float delta, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bind(textureLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);
        blankArea.forEach(image -> image.render(stack));
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 0x404040);
        font.draw(stack, inventory.getDisplayName(), 8, imageHeight - 96 + 2, 0x404040);
    }

    private void initializeSlots(PlayerInventory playerInventory) {
        for (int i = 0; i < menu.getInventory().getContainerSize(); i++) {
            int x = i % inventoryWidth;
            int y = (i - x) / inventoryWidth;
            menu.addClientSlot(new Slot(menu.getInventory(), i, x * Utils.SLOT_SIZE + 8, y * Utils.SLOT_SIZE + Utils.SLOT_SIZE));
        }
        int left = (inventoryWidth * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (inventoryHeight * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                menu.addClientSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            menu.addClientSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    @NotNull
    public List<Rectangle2d> getExclusionZones() {
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
