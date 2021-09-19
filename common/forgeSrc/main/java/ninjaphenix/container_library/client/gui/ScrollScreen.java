package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class ScrollScreen extends AbstractScreen {
    private static final int THUMB_WIDTH = 12, THUMB_HEIGHT = 15;
    private final ResourceLocation textureLocation;
    private final int textureWidth, textureHeight, totalRows, backgroundRenderWidth;
    private final boolean scrollingUnrestricted;
    private boolean isDragging, blankAreaVisible;
    private int topRow;
    // todo: consider making these ints
    private double scrollYOffset, thumbY;
    private @Nullable TexturedRect blankArea;

    public ScrollScreen(AbstractHandler handler, Inventory playerInventory, Component title, ScreenSize screenSize) {
        super(handler, playerInventory, title, screenSize);

        this.initializeSlots(playerInventory);

        textureLocation = new ResourceLocation("ninjaphenix_container_lib", "textures/gui/container/shared_" + menuWidth + "_" + menuHeight + ".png");
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

        totalRows = Mth.ceil(((double) totalSlots) / menuWidth);
        imageWidth = Utils.CONTAINER_PADDING_LDR + Utils.SLOT_SIZE * menuWidth + Utils.CONTAINER_PADDING_LDR + 22 - 4; // 22 - 4 is scrollbar width - overlap
        backgroundRenderWidth = imageWidth - 22 + 4; // - 22 + 4 is scrollbar width - overlap
        imageHeight = Utils.CONTAINER_HEADER_HEIGHT + Utils.SLOT_SIZE * menuHeight + 14 + Utils.SLOT_SIZE * 3 + 4 + Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR;
        scrollingUnrestricted = ConfigWrapper.getInstance().isScrollingUnrestricted();
    }

    private void initializeSlots(Inventory playerInventory) {
        for (int i = 0; i < totalSlots; i++) {
            int slotXPos = i % menuWidth;
            int slotYPos = Mth.ceil((((double) (i - slotXPos)) / menuWidth));
            int realYPos = slotYPos >= menuHeight ? -2000 : slotYPos * Utils.SLOT_SIZE + Utils.SLOT_SIZE;
            menu.addClientSlot(new Slot(menu.getInventory(), i, slotXPos * Utils.SLOT_SIZE + 8, realYPos));
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

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, backgroundRenderWidth, imageHeight, textureWidth, textureHeight);

        int containerSlotsHeight = menuHeight * 18;
        int scrollbarHeight = containerSlotsHeight + (menuWidth > 9 ? 34 : 24);
        GuiComponent.blit(stack, leftPos + backgroundRenderWidth - 4, topPos, backgroundRenderWidth, 0, 22, scrollbarHeight, textureWidth, textureHeight);

        GuiComponent.blit(stack, leftPos + backgroundRenderWidth - 2, topPos + Utils.CONTAINER_HEADER_HEIGHT + 1 + (int) thumbY, backgroundRenderWidth, scrollbarHeight, ScrollScreen.THUMB_WIDTH, ScrollScreen.THUMB_HEIGHT, textureWidth, textureHeight);

        if (blankArea != null && blankAreaVisible) {
            blankArea.render(stack);
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 0x404040);
        font.draw(stack, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040);
    }

    private boolean isMouseOverTrack(double mouseX, double mouseY) {
        int scrollbarTopPos = topPos + 18;
        int scrollbarLeftPos = leftPos + imageWidth - 2;
        return mouseX >= scrollbarLeftPos && mouseY >= scrollbarTopPos && mouseX < scrollbarLeftPos + 12 && mouseY < scrollbarTopPos + menuHeight * 18;
    }

    private boolean isMouseOverThumb(double mouseX, double mouseY) {
        boolean xCheck = leftPos + imageWidth - 20 <= mouseX && mouseX <= leftPos + imageWidth - 8;
        double correctedThumbY = topPos + Utils.CONTAINER_HEADER_HEIGHT + 1 + thumbY;
        return xCheck && correctedThumbY <= mouseY && mouseY <= correctedThumbY + ScrollScreen.THUMB_HEIGHT;
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button); // || this.isMouseOverEmptyRegionUnderScrollbar(mouseX, mouseY, left, top, button);
    }

    @Override
    protected boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            if (topRow != totalRows - menuHeight) {
                if (Screen.hasShiftDown()) {
                    this.setTopRowAndMoveThumb(topRow, Math.min(topRow + menuHeight, totalRows - menuHeight));
                } else {
                    this.setTopRowAndMoveThumb(topRow, topRow + 1);
                }
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            if (topRow != 0) {
                if (Screen.hasShiftDown()) {
                    this.setTopRowAndMoveThumb(topRow, Math.max(topRow - menuHeight, 0));
                } else {
                    this.setTopRowAndMoveThumb(topRow, topRow - 1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOverThumb(mouseX, mouseY) && button == 0) {
            scrollYOffset = mouseY - thumbY;
            isDragging = true;
        } else if (this.isMouseOverTrack(mouseX, mouseY) && button == 0) {
            //this.moveThumb(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            this.updateThumbPosition(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateThumbPosition(double mouseY) {
        thumbY = Math.min(Math.max(mouseY - scrollYOffset, 0), menuHeight * Utils.SLOT_SIZE - 2 - ScrollScreen.THUMB_HEIGHT);
        //this.setTopRow(topRow, MathHelper.floor(MathHelper.clampedLerp(0, totalRows - menuHeight, (mouseY - (y + 18)) / (menuHeight * 18))));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        //if (scrollingUnrestricted || this.isMouseOverTrack(mouseX, mouseY)) {
        //    int newTop;
        //    if (delta < 0) {
        //        newTop = Math.min(topRow + (Screen.hasShiftDown() ? menuHeight : 1), totalRows - menuHeight);
        //    } else {
        //        newTop = Math.max(topRow - (Screen.hasShiftDown() ? menuHeight : 1), 0);
        //    }
        //    this.setTopRow(topRow, newTop);
        //    return true;
        //}
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRowAndMoveThumb(int oldTopRow, int newTopRow) {

    }

    private void setTopRow(int oldTopRow, int newTopRow) {
        //if (oldTopRow == newTopRow) {
        //    return;
        //}
        //topRow = newTopRow;
        //blankAreaVisible = topRow == (totalRows - menuHeight);
        //int delta = newTopRow - oldTopRow;
        //int rows = Math.abs(delta);
        //if (rows < menuHeight) {
        //    int setAmount = rows * menuWidth;
        //    int movableAmount = (menuHeight - rows) * menuWidth;
        //    if (delta > 0) {
        //        int setOutBegin = oldTopRow * menuWidth;
        //        int movableBegin = newTopRow * menuWidth;
        //        int setInBegin = movableBegin + movableAmount;
        //        handler.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
        //        handler.moveSlotRange(movableBegin, setInBegin, -18 * rows);
        //        handler.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, totalSlots),
        //                index -> 18 * MathHelper.floorDiv(index - movableBegin + menuWidth, menuWidth));
        //    } else {
        //        int setInBegin = newTopRow * menuWidth;
        //        int movableBegin = oldTopRow * menuWidth;
        //        int setOutBegin = movableBegin + movableAmount;
        //        handler.setSlotRange(setInBegin, setInBegin + setAmount,
        //                index -> 18 * MathHelper.floorDiv(index - setInBegin + menuWidth, menuWidth));
        //        handler.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
        //        handler.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, totalSlots), index -> -2000);
        //    }
        //} else {
        //    int oldMin = oldTopRow * menuWidth;
        //    handler.setSlotRange(oldMin, Math.min(oldMin + menuWidth * menuHeight, totalSlots), index -> -2000);
        //    int newMin = newTopRow * menuWidth;
        //    handler.setSlotRange(newMin, newMin + menuWidth * menuHeight,
        //            index -> 18 + 18 * MathHelper.floorDiv(index - newMin, menuWidth));
        //}
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        int row = topRow;
        super.resize(client, width, height);
        this.setTopRowAndMoveThumb(topRow, row);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @NotNull
    @Override
    public List<Rect2i> getExclusionZones() {
        int height = Utils.CONTAINER_HEADER_HEIGHT + menuHeight * Utils.SLOT_SIZE + (menuWidth > 9 ? 10 : 0) + Utils.CONTAINER_PADDING_LDR;
        return Collections.singletonList(new Rect2i(leftPos + backgroundRenderWidth, topPos, 22 - 4, height)); // 22 - 4 is scrollbar width minus overlap
    }

    public static ScreenSize retrieveScreenSize(int slots, int scaledWidth, int scaledHeight) {
        ArrayList<ScreenSize> options = new ArrayList<>();
        options.add(ScreenSize.of(9, 6));
        if (scaledHeight >= 276) {
            if (slots > 54) {
                options.add(ScreenSize.of(9, 9));
            }
            if (scaledWidth >= 248 && slots > 81) {
                options.add(ScreenSize.of(12, 9));
            }
            if (scaledWidth >= 302 && slots > 108) {
                options.add(ScreenSize.of(15, 9));
            }
            if (scaledWidth >= 356 && slots > 135) {
                options.add(ScreenSize.of(18, 9));
            }
        }
        if (scaledHeight >= 330 && scaledWidth >= 356 && slots > 162) {
            options.add(ScreenSize.of(18, 12));
        }
        if (scaledHeight >= 384 && scaledWidth >= 356 && slots > 216) {
            options.add(ScreenSize.of(18, 15));
        }

        return options.get(options.size() - 1);
    }
}
