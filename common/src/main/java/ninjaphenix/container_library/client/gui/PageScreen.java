package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.client.gui.widget.PageButton;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PageScreen extends AbstractScreen {
    private final ResourceLocation textureLocation;
    private final int textureWidth, textureHeight;
    private final Set<TexturedRect> blankArea = new LinkedHashSet<>();
    private final int blankSlots, pages;
    private PageButton leftPageButton, rightPageButton;
    private int page;
    private TranslatableComponent currentPageText;
    private float pageTextX;

    public PageScreen(AbstractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.initializeSlots(playerInventory);

        textureLocation = new ResourceLocation("ninjaphenix_container_lib", "textures/gui/container/shared_" + menuWidth + "_" + menuHeight + ".png");
        textureWidth = 208;
        textureHeight = switch (menuHeight) {
            case 3 -> 192;
            case 6 -> 240;
            default -> throw new IllegalStateException("Unexpected value: " + menuHeight);
        };

        int slotsPerPage = menuWidth * menuHeight;
        pages = Mth.ceil((double) totalSlots / slotsPerPage);

        blankSlots = slotsPerPage - (totalSlots % slotsPerPage);

        imageWidth = 14 + 18 * menuWidth;
        imageHeight = 17 + 97 + 18 * menuHeight;
    }

    private static boolean regionIntersects(AbstractWidget widget, int x, int y, int width, int height) {
        return widget.x <= x + width && y <= widget.y + widget.getHeight() ||
                x <= widget.x + widget.getWidth() && widget.y <= y + height;
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, textureWidth, textureHeight);
        blankArea.forEach(image -> image.render(stack));
    }

    private void initializeSlots(Inventory playerInventory) {
        menu.resetSlotPositions(true, menuWidth, menuHeight);
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

    private void setPage(int oldPage, int newPage) {
        if (newPage == 0 || newPage > pages) {
            return;
        }
        page = newPage;
        if (newPage > oldPage) {
            if (page == pages) {
                rightPageButton.setActive(false);
                if (blankSlots > 0) {
                    int rows = Mth.intFloorDiv(blankSlots, menuWidth);
                    int remainder = (blankSlots - menuWidth * rows);
                    int yTop = topPos + Utils.CONTAINER_HEADER_HEIGHT + (menuHeight - 1) * Utils.SLOT_SIZE;
                    int xLeft = leftPos + Utils.CONTAINER_PADDING_WIDTH;
                    for (int i = 0; i < rows; i++) {
                        blankArea.add(new TexturedRect(xLeft, yTop, menuWidth * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, textureWidth, textureHeight));
                        yTop -= Utils.SLOT_SIZE;
                    }
                    if (remainder > 0) {
                        int xRight = leftPos + Utils.CONTAINER_PADDING_WIDTH + menuWidth * Utils.SLOT_SIZE;
                        int width = remainder * Utils.SLOT_SIZE;
                        blankArea.add(new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, textureWidth, textureHeight));
                    }
                }
            }
            if (!leftPageButton.active) {
                leftPageButton.setActive(true);
            }
        } else if (newPage < oldPage) {
            if (page == 1) {
                leftPageButton.setActive(false);
            }
            blankArea.clear();
            if (!rightPageButton.active) {
                rightPageButton.setActive(true);
            }
        }
        int slotsPerPage = menuWidth * menuHeight;
        int oldMin = slotsPerPage * (oldPage - 1);
        int oldMax = Math.min(oldMin + slotsPerPage, totalSlots);
        menu.moveSlotRange(oldMin, oldMax, -2000);
        int newMin = slotsPerPage * (newPage - 1);
        int newMax = Math.min(newMin + slotsPerPage, totalSlots);
        menu.moveSlotRange(newMin, newMax, 2000);
        this.setPageText();
    }

    private void setPageText() {
        currentPageText = new TranslatableComponent("screen.ninjaphenix_container_lib.page_x_y", page, pages);
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        super.renderTooltip(stack, mouseX, mouseY);
        leftPageButton.renderTooltip(stack, mouseX, mouseY);
        rightPageButton.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        int currentPage = page;
        if (currentPage != 1) {
            menu.resetSlotPositions(false, menuWidth, menuHeight);
            super.resize(client, width, height);
            blankArea.clear();
            this.setPage(1, currentPage);
            return;
        }
        super.resize(client, width, height);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 0x404040);
        font.draw(stack, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040);
        if (currentPageText != null) {
            font.draw(stack, currentPageText.getVisualOrderText(), pageTextX - leftPos, imageHeight - 94, 0x404040);
        }
    }

    @Override
    protected boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            this.setPage(page, Screen.hasShiftDown() ? pages : page + 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            this.setPage(page, Screen.hasShiftDown() ? 1 : page - 1);
            return true;
        }
        return false;
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }

    public void addPageButtons() {
        int width = 54;
        int x = leftPos + imageWidth - 61;
        int originalX = x;
        int y = topPos + imageHeight - 96;
        List<AbstractWidget> renderableChildren = new ArrayList<>();
        for (var child : this.children()) {
            if (child instanceof AbstractWidget widget) {
                renderableChildren.add(widget);
            }
        }
        renderableChildren.sort(Comparator.comparingInt(a -> -a.x));
        for (AbstractWidget widget : renderableChildren) {
            if (PageScreen.regionIntersects(widget, x, y, width, 12)) {
                x = widget.x - width - 2;
            }
        }
        page = 1;
        this.setPageText();
        // Honestly this is dumb.
        if (x == originalX && PlatformUtils.isModLoaded("inventoryprofiles")) {
            x -= 14;
        }
        leftPageButton = new PageButton(x, y, 0,
                new TranslatableComponent("screen.ninjaphenix_container_lib.prev_page"), button -> this.setPage(page, page - 1),
                this::renderButtonTooltip);
        leftPageButton.active = false;
        this.addRenderableWidget(leftPageButton);
        rightPageButton = new PageButton(x + 42, y, 1,
                new TranslatableComponent("screen.ninjaphenix_container_lib.next_page"), button -> this.setPage(page, page + 1),
                this::renderButtonTooltip);
        this.addRenderableWidget(rightPageButton);
        pageTextX = (1 + leftPageButton.x + rightPageButton.x - rightPageButton.getWidth() / 2F) / 2F;
    }

    private void renderButtonTooltip(AbstractButton button, PoseStack stack, int x, int y) {
        this.renderTooltip(stack, button.getMessage(), x, y);
    }
}
