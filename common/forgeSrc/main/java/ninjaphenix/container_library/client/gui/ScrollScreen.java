package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
    private final ResourceLocation textureLocation;
    private final int textureWidth, textureHeight, totalRows;
    private final boolean scrollingUnrestricted;
    private boolean isDragging, blankAreaVisible;
    private int topRow;
    private @Nullable TexturedRect blankArea;

    public ScrollScreen(AbstractMenu menu, Inventory playerInventory, Component title, ScreenSize screenSize) {
        super(menu, playerInventory, title, screenSize);

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
            default -> throw new IllegalStateException("Unexpected value: " + menuHeight);
        };

        totalRows = Mth.ceil(((double) totalSlots) / menuWidth);
        imageWidth = 14 + 18 * menuWidth;
        imageHeight = 17 + 97 + 18 * menuHeight;
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
    protected void init() {
        super.init();
        isDragging = false;
        topRow = 0;

        int remainderSlots = (totalSlots % menuWidth);
        if (remainderSlots > 0) {
            int blankSlots = menuWidth - remainderSlots;
            int xRight = leftPos + Utils.CONTAINER_PADDING_WIDTH + menuWidth * Utils.SLOT_SIZE;
            int y = this.topPos + Utils.CONTAINER_HEADER_HEIGHT + (menuHeight - 1) * Utils.SLOT_SIZE;
            int width = blankSlots * Utils.SLOT_SIZE;
            blankArea = new TexturedRect(xRight - width, y, width, Utils.SLOT_SIZE, Utils.CONTAINER_PADDING_WIDTH, imageHeight, textureWidth, textureHeight);
            blankAreaVisible = false;
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);
        int containerSlotsHeight = menuHeight * 18;
        int scrollbarHeight = containerSlotsHeight + (menuWidth > 9 ? 34 : 24);
        GuiComponent.blit(stack, leftPos + imageWidth - 4, topPos, imageWidth, 0, 22, scrollbarHeight, textureWidth, textureHeight);
        int yOffset = Mth.floor((containerSlotsHeight - 17) * (((double) topRow) / (totalRows - menuHeight)));
        GuiComponent.blit(stack, leftPos + imageWidth - 2, topPos + yOffset + 18, imageWidth, scrollbarHeight, 12, 15, textureWidth, textureHeight);
        if (blankArea != null && blankAreaVisible) {
            blankArea.render(stack);
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 0x404040);
        font.draw(stack, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040);
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        int scrollbarTopPos = topPos + 18;
        int scrollbarLeftPos = leftPos + imageWidth - 2;
        return mouseX >= scrollbarLeftPos && mouseY >= scrollbarTopPos && mouseX < scrollbarLeftPos + 12 && mouseY < scrollbarTopPos + menuHeight * 18;
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button) && !this.isMouseOverScrollbar(mouseX, mouseY);
    }

    @Override
    protected boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            if (topRow != totalRows - menuHeight) {
                if (Screen.hasShiftDown()) {
                    this.setTopRow(topRow, Math.min(topRow + menuHeight, totalRows - menuHeight));
                } else {
                    this.setTopRow(topRow, topRow + 1);
                }
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            if (topRow != 0) {
                if (Screen.hasShiftDown()) {
                    this.setTopRow(topRow, Math.max(topRow - menuHeight, 0));
                } else {
                    this.setTopRow(topRow, topRow - 1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOverScrollbar(mouseX, mouseY) && button == 0) {
            isDragging = true;
            this.updateTopRow(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            this.updateTopRow(mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateTopRow(double mouseY) {
        this.setTopRow(topRow, Mth.floor(Mth.clampedLerp(0, totalRows - menuHeight, (mouseY - (topPos + 18)) / (menuHeight * 18))));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (scrollingUnrestricted || this.isMouseOverScrollbar(mouseX, mouseY)) {
            int newTop;
            if (delta < 0) {
                newTop = Math.min(topRow + (Screen.hasShiftDown() ? menuHeight : 1), totalRows - menuHeight);
            } else {
                newTop = Math.max(topRow - (Screen.hasShiftDown() ? menuHeight : 1), 0);
            }
            this.setTopRow(topRow, newTop);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRow(int oldTopRow, int newTopRow) {
        if (oldTopRow == newTopRow) {
            return;
        }
        topRow = newTopRow;
        blankAreaVisible = topRow == (totalRows - menuHeight);
        int delta = newTopRow - oldTopRow;
        int rows = Math.abs(delta);
        if (rows < menuHeight) {
            int setAmount = rows * menuWidth;
            int movableAmount = (menuHeight - rows) * menuWidth;
            if (delta > 0) {
                int setOutBegin = oldTopRow * menuWidth;
                int movableBegin = newTopRow * menuWidth;
                int setInBegin = movableBegin + movableAmount;
                menu.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
                menu.moveSlotRange(movableBegin, setInBegin, -18 * rows);
                menu.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, totalSlots),
                        index -> 18 * Mth.intFloorDiv(index - movableBegin + menuWidth, menuWidth));
            } else {
                int setInBegin = newTopRow * menuWidth;
                int movableBegin = oldTopRow * menuWidth;
                int setOutBegin = movableBegin + movableAmount;
                menu.setSlotRange(setInBegin, setInBegin + setAmount,
                        index -> 18 * Mth.intFloorDiv(index - setInBegin + menuWidth, menuWidth));
                menu.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
                menu.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, totalSlots), index -> -2000);
            }
        } else {
            int oldMin = oldTopRow * menuWidth;
            menu.setSlotRange(oldMin, Math.min(oldMin + menuWidth * menuHeight, totalSlots), index -> -2000);
            int newMin = newTopRow * menuWidth;
            menu.setSlotRange(newMin, newMin + menuWidth * menuHeight,
                    index -> 18 + 18 * Mth.intFloorDiv(index - newMin, menuWidth));
        }
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        int row = topRow;
        super.resize(client, width, height);
        this.setTopRow(topRow, row);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public List<Rect2i> getExclusionZones() {
        int height = menuHeight * 18 + (menuWidth > 9 ? 34 : 24);
        return Collections.singletonList(new Rect2i(leftPos + imageWidth - 4, topPos, 22, height));
    }
}
