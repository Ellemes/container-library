package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.internal.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.internal.api.client.gui.TexturedRect;
import ninjaphenix.container_library.internal.api.client.gui.widget.PageButton;
import ninjaphenix.container_library.inventory.PagedMenu;
import ninjaphenix.container_library.inventory.screen.PagedScreenMeta;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PagedScreen extends AbstractScreen<PagedMenu, PagedScreenMeta> {
    private final Set<TexturedRect> blankArea = new LinkedHashSet<>();
    private PageButton leftPageButton;
    private PageButton rightPageButton;
    private int page;
    private TranslatableComponent currentPageText;
    private float pageTextX;

    public PagedScreen(PagedMenu screenHandler, Inventory playerInventory, Component title) {
        super(screenHandler, playerInventory, title, (screenMeta) -> (screenMeta.width * 18 + 14) / 2 - 80);
        imageWidth = 14 + 18 * screenMeta.width;
        imageHeight = 17 + 97 + 18 * screenMeta.height;
    }

    private static boolean regionIntersects(AbstractWidget widget, int x, int y, int width, int height) {
        return widget.x <= x + width && y <= widget.y + widget.getHeight() ||
                x <= widget.x + widget.getWidth() && widget.y <= y + height;
    }

    private void setPage(int oldPage, int newPage) {
        if (newPage == 0 || newPage > screenMeta.pages) {
            return;
        }
        page = newPage;
        if (newPage > oldPage) {
            if (page == screenMeta.pages) {
                rightPageButton.setActive(false);
                int blanked = screenMeta.blankSlots;
                if (blanked > 0) {
                    int rows = Mth.intFloorDiv(blanked, screenMeta.width);
                    int remainder = (blanked - screenMeta.width * rows);
                    int yTop = topPos + Utils.CONTAINER_HEADER_HEIGHT + (screenMeta.height - 1) * Utils.SLOT_SIZE;
                    int xLeft = leftPos + Utils.CONTAINER_PADDING_WIDTH;
                    for (int i = 0; i < rows; i++) {
                        blankArea.add(new TexturedRect(xLeft, yTop, screenMeta.width * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight));
                        yTop -= Utils.SLOT_SIZE;
                    }
                    if (remainder > 0) {
                        int xRight = leftPos + Utils.CONTAINER_PADDING_WIDTH + screenMeta.width * Utils.SLOT_SIZE;
                        int width = remainder * Utils.SLOT_SIZE;
                        blankArea.add(new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight));
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
        int slotsPerPage = screenMeta.width * screenMeta.height;
        int oldMin = slotsPerPage * (oldPage - 1);
        int oldMax = Math.min(oldMin + slotsPerPage, screenMeta.totalSlots);
        menu.moveSlotRange(oldMin, oldMax, -2000);
        int newMin = slotsPerPage * (newPage - 1);
        int newMax = Math.min(newMin + slotsPerPage, screenMeta.totalSlots);
        menu.moveSlotRange(newMin, newMax, 2000);
        this.setPageText();
    }

    private void setPageText() {
        currentPageText = new TranslatableComponent("screen.expandedstorage.page_x_y", page, screenMeta.pages);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        if (this.hasPages()) {
            leftPageButton.renderTooltip(stack, mouseX, mouseY);
            rightPageButton.renderTooltip(stack, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        super.renderBg(stack, delta, mouseX, mouseY);
        blankArea.forEach(image -> image.render(stack));
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        if (this.hasPages()) {
            int currentPage = page;
            if (currentPage != 1) {
                menu.resetSlotPositions(false);
                super.resize(client, width, height);
                blankArea.clear();
                this.setPage(1, currentPage);
                return;
            }
        }
        super.resize(client, width, height);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        if (currentPageText != null) {
            font.draw(stack, currentPageText.getVisualOrderText(), pageTextX - leftPos, imageHeight - 94, 0x404040);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.hasPages()) {
            if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                this.setPage(page, hasShiftDown() ? screenMeta.pages : page + 1);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                this.setPage(page, hasShiftDown() ? 1 : page - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }

    private boolean hasPages() {
        return screenMeta.pages != 1;
    }

    public void addPageButtons() {
        if (this.hasPages()) {
            int width = 54;
            int x = leftPos + imageWidth - 61;
            int originalX = x;
            int y = topPos + imageHeight - 96;
            var renderableChildren = new ArrayList<AbstractWidget>();
            for (var child : this.children()) {
                if (child instanceof AbstractWidget widget) {
                    renderableChildren.add(widget);
                }
            }
            renderableChildren.sort(Comparator.comparingInt(a -> -a.x));
            for (var widget : renderableChildren) {
                if (PagedScreen.regionIntersects(widget, x, y, width, 12)) {
                    x = widget.x - width - 2;
                }
            }
            page = 1;
            this.setPageText();
            // Honestly this is dumb.
            if (x == originalX && PlatformUtils.getInstance().isModLoaded("inventoryprofiles")) {
                x -= 14;
            }
            leftPageButton = new PageButton(x, y, 0,
                    new TranslatableComponent("screen.expandedstorage.prev_page"), button -> this.setPage(page, page - 1),
                    this::renderButtonTooltip);
            leftPageButton.active = false;
            this.addRenderableWidget(leftPageButton);
            rightPageButton = new PageButton(x + 42, y, 1,
                    new TranslatableComponent("screen.expandedstorage.next_page"), button -> this.setPage(page, page + 1),
                    this::renderButtonTooltip);
            this.addRenderableWidget(rightPageButton);
            pageTextX = (1 + leftPageButton.x + rightPageButton.x - rightPageButton.getWidth() / 2F) / 2F;
        }
    }
}
