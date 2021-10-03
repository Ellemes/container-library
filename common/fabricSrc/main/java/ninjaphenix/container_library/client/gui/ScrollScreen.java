package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
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
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScrollScreen extends AbstractScreen {
    private static final int THUMB_WIDTH = 12, THUMB_HEIGHT = 15;
    private final Identifier textureLocation;
    private final int textureWidth, textureHeight, totalRows;
    private final boolean scrollingUnrestricted;
    private boolean isDragging, blankAreaVisible;
    private int topRow, scrollYOffset, thumbY, blankSlots;
    private @Nullable TexturedRect blankArea;

    public ScrollScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title, ScreenSize screenSize) {
        super(handler, playerInventory, title, screenSize);

        this.initializeSlots(playerInventory);

        textureLocation = new Identifier("ninjaphenix_container_lib", "textures/gui/container/shared_" + inventoryWidth + "_" + inventoryHeight + ".png");

        if (inventoryWidth == 9) {
            textureWidth = 208;
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

        totalRows = MathHelper.ceil(((double) totalSlots) / inventoryWidth);
        backgroundWidth = Utils.CONTAINER_PADDING_LDR + Utils.SLOT_SIZE * inventoryWidth + Utils.CONTAINER_PADDING_LDR; // 22 - 4 is scrollbar width - overlap
        backgroundHeight = Utils.CONTAINER_HEADER_HEIGHT + Utils.SLOT_SIZE * inventoryHeight + 14 + Utils.SLOT_SIZE * 3 + 4 + Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR;
        scrollingUnrestricted = ConfigWrapper.getInstance().isScrollingUnrestricted();
    }

    private void initializeSlots(PlayerInventory playerInventory) {
        for (int i = 0; i < totalSlots; i++) {
            int slotXPos = i % inventoryWidth;
            int slotYPos = MathHelper.ceil((((double) (i - slotXPos)) / inventoryWidth));
            int realYPos = slotYPos >= inventoryHeight ? -2000 : slotYPos * Utils.SLOT_SIZE + Utils.SLOT_SIZE;
            handler.addClientSlot(new Slot(handler.getInventory(), i, slotXPos * Utils.SLOT_SIZE + 8, realYPos));
        }
        int left = (inventoryWidth * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (inventoryHeight * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                handler.addClientSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            handler.addClientSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    @Override
    protected void init() {
        super.init();
        x = (width - (backgroundWidth + 22 - 4)) / 2;
        isDragging = false;
        topRow = 0;

        int remainderSlots = (totalSlots % inventoryWidth);
        if (remainderSlots > 0) {
            blankSlots = inventoryWidth - remainderSlots;
            int xRight = x + Utils.CONTAINER_PADDING_LDR + inventoryWidth * Utils.SLOT_SIZE;
            int yTop = y + Utils.CONTAINER_HEADER_HEIGHT + (inventoryHeight - 1) * Utils.SLOT_SIZE;
            int width = blankSlots * Utils.SLOT_SIZE;
            blankArea = new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE, Utils.CONTAINER_PADDING_LDR, backgroundHeight, textureWidth, textureHeight);
            blankAreaVisible = false;
        }
    }

    @Override
    protected void drawBackground(MatrixStack stack, float delta, int mouseX, int mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(textureLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(stack, x, y, 0, 0, backgroundWidth, backgroundHeight, textureWidth, textureHeight);

        int containerSlotsHeight = inventoryHeight * 18;
        int scrollbarHeight = containerSlotsHeight + (inventoryWidth > 9 ? 34 : 24);
        DrawableHelper.drawTexture(stack, x + backgroundWidth - 4, y, backgroundWidth, 0, 22, scrollbarHeight, textureWidth, textureHeight);

        DrawableHelper.drawTexture(stack, x + backgroundWidth - 2, y + Utils.CONTAINER_HEADER_HEIGHT + 1 + thumbY, backgroundWidth, scrollbarHeight, ScrollScreen.THUMB_WIDTH, ScrollScreen.THUMB_HEIGHT, textureWidth, textureHeight);

        if (blankArea != null && blankAreaVisible) {
            blankArea.render(stack);
        }
    }

    @Override
    protected void drawForeground(MatrixStack stack, int mouseX, int mouseY) {
        textRenderer.draw(stack, title, 8, 6, 0x404040);
        textRenderer.draw(stack, playerInventory.getDisplayName(), 8, backgroundHeight - 96 + 2, 0x404040);
    }

    private boolean isMouseOverTrack(double mouseX, double mouseY) {
        boolean xCheck = x + backgroundWidth - 2 <= mouseX && mouseX <= x + backgroundWidth - 2 + ScrollScreen.THUMB_WIDTH;
        int scrollbarStart = y + Utils.CONTAINER_HEADER_HEIGHT + 1;
        return xCheck && scrollbarStart <= mouseY && mouseY <= scrollbarStart + inventoryHeight * 18 - 2;
    }

    private boolean isMouseOverThumb(double mouseX, double mouseY) {
        boolean xCheck = x + backgroundWidth - 2 <= mouseX && mouseX <= x + backgroundWidth - 2 + ScrollScreen.THUMB_WIDTH;
        double correctedThumbY = y + Utils.CONTAINER_HEADER_HEIGHT + 1 + thumbY;
        return xCheck && correctedThumbY <= mouseY && mouseY <= correctedThumbY + ScrollScreen.THUMB_HEIGHT;
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY, int left, int top) {
        return mouseX > left + backgroundWidth - 4 && mouseX <= left + backgroundWidth + 22 &&
                mouseY >= top && mouseY < top + Utils.CONTAINER_HEADER_HEIGHT + inventoryHeight * 18 + (inventoryWidth > 9 ? 10 : 0) + Utils.CONTAINER_PADDING_LDR;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        if (inventoryWidth > 9 && mouseY >= top + Utils.CONTAINER_HEADER_HEIGHT + inventoryHeight * Utils.SLOT_SIZE + Utils.CONTAINER_HEADER_HEIGHT) {
            int outsideRegion = (backgroundWidth - (Utils.CONTAINER_PADDING_LDR + 9 * Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR)) / 2;
            if (mouseX < left + outsideRegion || mouseX > left + backgroundWidth - outsideRegion) {
                return true;
            }
        }
        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button) && !this.isMouseOverScrollbar(mouseX, mouseY, x, y);
    }

    @Override
    protected boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            if (topRow != totalRows - inventoryHeight) {
                if (Screen.hasShiftDown()) {
                    this.setTopRowAndMoveThumb(topRow, Math.min(topRow + inventoryHeight, totalRows - inventoryHeight));
                } else {
                    this.setTopRowAndMoveThumb(topRow, topRow + 1);
                }
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            if (topRow != 0) {
                if (Screen.hasShiftDown()) {
                    this.setTopRowAndMoveThumb(topRow, Math.max(topRow - inventoryHeight, 0));
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
            scrollYOffset = (int) mouseY - thumbY;
            isDragging = true;
        } else if (this.isMouseOverTrack(mouseX, mouseY) && button == 0) {
            this.updateThumbPosition(mouseY - Utils.CONTAINER_HEADER_HEIGHT - 1 - y);
            this.snapThumbToGradation();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            this.updateThumbPosition(mouseY - scrollYOffset);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            this.snapThumbToGradation();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void snapThumbToGradation() {
        thumbY = (int) (((double) topRow / (totalRows - inventoryHeight)) * (inventoryHeight * Utils.SLOT_SIZE - 2 - ScrollScreen.THUMB_HEIGHT));
    }

    private void updateThumbPosition(double adjustedMouseY) {
        thumbY = (int) Math.min(Math.max(adjustedMouseY, 0), inventoryHeight * Utils.SLOT_SIZE - 2 - ScrollScreen.THUMB_HEIGHT);
        int row = (int) Math.round(((double) thumbY) / (inventoryHeight * Utils.SLOT_SIZE - 2 - ScrollScreen.THUMB_HEIGHT) * (totalRows - inventoryHeight));
        this.setTopRow(topRow, row);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (scrollingUnrestricted || this.isMouseOverTrack(mouseX, mouseY)) {
            int newTop;
            if (delta < 0) {
                newTop = Math.min(topRow + (Screen.hasShiftDown() ? inventoryHeight : 1), totalRows - inventoryHeight);
            } else {
                newTop = Math.max(topRow - (Screen.hasShiftDown() ? inventoryHeight : 1), 0);
            }
            this.setTopRowAndMoveThumb(topRow, newTop);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRowAndMoveThumb(int oldTopRow, int newTopRow) {
        this.setTopRow(oldTopRow, newTopRow);
        this.snapThumbToGradation();
    }

    private void setTopRow(int oldTopRow, int newTopRow) {
        if (oldTopRow == newTopRow) {
            return;
        }
        topRow = newTopRow;
        blankAreaVisible = topRow == (totalRows - inventoryHeight);
        int delta = newTopRow - oldTopRow;
        int rows = Math.abs(delta);
        if (rows < inventoryHeight) {
            int setAmount = rows * inventoryWidth;
            int movableAmount = (inventoryHeight - rows) * inventoryWidth;
            if (delta > 0) {
                int setOutBegin = oldTopRow * inventoryWidth;
                int movableBegin = newTopRow * inventoryWidth;
                int setInBegin = movableBegin + movableAmount;
                handler.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
                handler.moveSlotRange(movableBegin, setInBegin, -18 * rows);
                handler.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, totalSlots),
                        index -> 18 * MathHelper.floorDiv(index - movableBegin + inventoryWidth, inventoryWidth));
            } else {
                int setInBegin = newTopRow * inventoryWidth;
                int movableBegin = oldTopRow * inventoryWidth;
                int setOutBegin = movableBegin + movableAmount;
                handler.setSlotRange(setInBegin, setInBegin + setAmount,
                        index -> 18 * MathHelper.floorDiv(index - setInBegin + inventoryWidth, inventoryWidth));
                handler.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
                handler.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, totalSlots), index -> -2000);
            }
        } else {
            int oldMin = oldTopRow * inventoryWidth;
            handler.setSlotRange(oldMin, Math.min(oldMin + inventoryWidth * inventoryHeight, totalSlots), index -> -2000);
            int newMin = newTopRow * inventoryWidth;
            handler.setSlotRange(newMin, newMin + inventoryWidth * inventoryHeight - (blankAreaVisible ? blankSlots : 0),
                    index -> 18 + 18 * MathHelper.floorDiv(index - newMin, inventoryWidth));
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        int row = topRow;
        super.resize(client, width, height);
        this.setTopRowAndMoveThumb(topRow, row);
    }

    @NotNull
    @Override
    public List<Rect2i> getExclusionZones() {
        int height = Utils.CONTAINER_HEADER_HEIGHT + inventoryHeight * Utils.SLOT_SIZE + (inventoryWidth > 9 ? 10 : 0) + Utils.CONTAINER_PADDING_LDR;
        return Collections.singletonList(new Rect2i(x + backgroundWidth, y, 22 - 4, height)); // 22 - 4 is scrollbar width minus overlap
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
