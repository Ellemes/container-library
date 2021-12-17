package ninjaphenix.container_library.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.client.gui.TexturedRect;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.client.gui.widget.PageButton;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import ninjaphenix.container_library.wrappers.PlatformUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public final class PageScreen extends AbstractScreen {
    private final Identifier textureLocation;
    private final int textureWidth, textureHeight;
    private final Set<TexturedRect> blankArea = new LinkedHashSet<>();
    private final int blankSlots, pages;
    private PageButton leftPageButton, rightPageButton;
    private int page;
    private TranslatableText currentPageText;
    private float pageTextX;

    public PageScreen(AbstractHandler handler, PlayerInventory playerInventory, Text title, ScreenSize screenSize) {
        super(handler, playerInventory, title, screenSize);

        this.initializeSlots(playerInventory);

        textureLocation = new Identifier("ninjaphenix_container_lib", "textures/gui/container/shared_" + inventoryWidth + "_" + inventoryHeight + ".png");

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
        } else {
            throw new IllegalStateException("Unexpected value: " + inventoryHeight);
        }

        int slotsPerPage = inventoryWidth * inventoryHeight;
        pages = MathHelper.ceil((double) totalSlots / slotsPerPage);
        int lastPageSlots = totalSlots - (pages - 1) * slotsPerPage;
        blankSlots = slotsPerPage - lastPageSlots;

        backgroundWidth = Utils.CONTAINER_PADDING_LDR + Utils.SLOT_SIZE * inventoryWidth + Utils.CONTAINER_PADDING_LDR;
        backgroundHeight = Utils.CONTAINER_HEADER_HEIGHT + Utils.SLOT_SIZE * inventoryHeight + 14 + Utils.SLOT_SIZE * 3 + 4 + Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR;
    }

    private static boolean regionIntersects(ClickableWidget widget, int x, int y, int width, int height) {
        return widget.x <= x + width && y <= widget.y + widget.getHeight() || x <= widget.x + widget.getWidth() && widget.y <= y + height;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float delta, int mouseX, int mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(textureLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(stack, x, y, 0, 0, backgroundWidth, backgroundHeight, textureWidth, textureHeight);
        blankArea.forEach(image -> image.render(stack));
    }

    private void initializeSlots(PlayerInventory playerInventory) {
        handler.resetSlotPositions(true, inventoryWidth, inventoryHeight);
        int playerInvLeft = (inventoryWidth * Utils.SLOT_SIZE + 14) / 2 - 80;
        int playerInvTop = Utils.SLOT_SIZE + 14 + (inventoryHeight * Utils.SLOT_SIZE);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                handler.addClientSlot(new Slot(playerInventory, y * 9 + x + 9, playerInvLeft + Utils.SLOT_SIZE * x, playerInvTop + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            handler.addClientSlot(new Slot(playerInventory, x, playerInvLeft + Utils.SLOT_SIZE * x, playerInvTop + 58));
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        if (inventoryWidth > 9 && mouseY >= top + Utils.CONTAINER_HEADER_HEIGHT + inventoryHeight * Utils.SLOT_SIZE + Utils.CONTAINER_HEADER_HEIGHT) {
            int outsideRegion = (backgroundWidth - (Utils.CONTAINER_PADDING_LDR + 9 * Utils.SLOT_SIZE + Utils.CONTAINER_PADDING_LDR)) / 2;
            if (mouseX < left + outsideRegion || mouseX > left + backgroundWidth - outsideRegion) {
                return true;
            }
        }
        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button);
    }

    private void setPage(int oldPage, int newPage) {
        if (newPage == 0 || newPage > pages) {
            return;
        }
        page = newPage;
        if (newPage > oldPage) {
            if (page == pages) {
                rightPageButton.setActive(false);
                // todo: calculate blankArea once & add boolean field
                if (blankSlots > 0) {
                    int rows = MathHelper.floorDiv(blankSlots, inventoryWidth);
                    int remainder = (blankSlots - inventoryWidth * rows);
                    int yTop = y + Utils.CONTAINER_HEADER_HEIGHT + (inventoryHeight - 1) * Utils.SLOT_SIZE;
                    int xLeft = x + Utils.CONTAINER_PADDING_LDR;
                    for (int i = 0; i < rows; i++) {
                        blankArea.add(new TexturedRect(xLeft, yTop, inventoryWidth * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_LDR, backgroundHeight, textureWidth, textureHeight));
                        yTop -= Utils.SLOT_SIZE;
                    }
                    if (remainder > 0) {
                        int xRight = x + Utils.CONTAINER_PADDING_LDR + inventoryWidth * Utils.SLOT_SIZE;
                        int width = remainder * Utils.SLOT_SIZE;
                        blankArea.add(new TexturedRect(xRight - width, yTop, width, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_LDR, backgroundHeight, textureWidth, textureHeight));
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
        int slotsPerPage = inventoryWidth * inventoryHeight;
        int oldMin = slotsPerPage * (oldPage - 1);
        int oldMax = Math.min(oldMin + slotsPerPage, totalSlots);
        handler.moveSlotRange(oldMin, oldMax, -2000);
        int newMin = slotsPerPage * (newPage - 1);
        int newMax = Math.min(newMin + slotsPerPage, totalSlots);
        handler.moveSlotRange(newMin, newMax, 2000);
        this.setPageText();
    }

    private void setPageText() {
        currentPageText = new TranslatableText("screen.ninjaphenix_container_lib.page_x_y", page, pages);
        pageTextX = (leftPageButton.x + leftPageButton.getWidth() + rightPageButton.x) / 2 - textRenderer.getWidth(currentPageText) / 2 + 0.5f;
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        leftPageButton.renderButtonTooltip(stack, mouseX, mouseY);
        rightPageButton.renderButtonTooltip(stack, mouseX, mouseY);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        int currentPage = page;
        if (currentPage != 1) {
            handler.resetSlotPositions(false, inventoryWidth, inventoryHeight);
            super.resize(client, width, height);
            blankArea.clear();
            this.setPage(1, currentPage);
            return;
        }
        super.resize(client, width, height);
    }

    @Override
    protected void drawForeground(MatrixStack stack, int mouseX, int mouseY) {
        textRenderer.draw(stack, title, 8, 6, 0x404040);
        textRenderer.draw(stack, playerInventory.getDisplayName(), 8, backgroundHeight - 96 + 2, 0x404040);
        if (currentPageText != null) {
            textRenderer.draw(stack, currentPageText.asOrderedText(), pageTextX - x, backgroundHeight - 94, 0x404040);
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

    @NotNull
    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }

    public void addPageButtons() {
        int width = 54;
        int x = this.x + backgroundWidth - 61;
        int originalX = x;
        int y = this.y + backgroundHeight - 96;
        List<ClickableWidget> renderableChildren = new ArrayList<>();
        for (Element child : this.children()) {
            if (child instanceof ClickableWidget) {
                renderableChildren.add((ClickableWidget) child);
            }
        }
        renderableChildren.sort(Comparator.comparingInt(a -> -a.x));
        for (ClickableWidget widget : renderableChildren) {
            if (PageScreen.regionIntersects(widget, x, y, width, 12)) {
                x = widget.x - width - 2;
            }
        }
        page = 1;
        // Honestly this is dumb.
        if (x == originalX && PlatformUtils.isModLoaded("inventoryprofiles")) {
            x -= 14;
        }
        leftPageButton = new PageButton(x, y, 0,
                new TranslatableText("screen.ninjaphenix_container_lib.prev_page"), button -> this.setPage(page, page - 1),
                this::renderButtonTooltip);
        leftPageButton.active = false;
        this.addButton(leftPageButton);
        rightPageButton = new PageButton(x + 42, y, 1,
                new TranslatableText("screen.ninjaphenix_container_lib.next_page"), button -> this.setPage(page, page + 1),
                this::renderButtonTooltip);
        this.addButton(rightPageButton);
        this.setPageText();
    }

    private void renderButtonTooltip(PressableWidget button, MatrixStack stack, int x, int y) {
        this.renderTooltip(stack, button.getMessage(), x, y);
    }

    public static ScreenSize retrieveScreenSize(int slots, int scaledWidth, int scaledHeight) {
        ArrayList<Pair<ScreenSize, ScreenSize>> options = new ArrayList<>();
        PageScreen.addEntry(options, slots, 9, 3);
        PageScreen.addEntry(options, slots, 9, 6);
        if (scaledHeight >= 276 && slots > 54) {
            PageScreen.addEntry(options, slots, 9, 9);
        }
        Pair<ScreenSize, ScreenSize> picked = null;
        for (Pair<ScreenSize, ScreenSize> option : options) {
            if (picked == null) {
                picked = option;
            } else {
                ScreenSize pickedMeta = picked.getSecond();
                ScreenSize iterMeta = option.getSecond();
                ScreenSize iterDim = option.getFirst();
                if (pickedMeta.getHeight() == iterMeta.getHeight() && iterMeta.getWidth() < pickedMeta.getWidth()) {
                    picked = option;
                } else if (ConfigWrapper.getInstance().preferSmallerScreens() && pickedMeta.getWidth() == iterMeta.getWidth() + 1 && iterMeta.getHeight() <= iterDim.getWidth() * iterDim.getHeight() / 2.0) {

                } else if (iterMeta.getWidth() < pickedMeta.getWidth() && iterMeta.getHeight() <= iterDim.getWidth() * iterDim.getHeight() / 2.0) {
                    picked = option;
                }
            }
        }
        return picked.getFirst();
    }

    private static void addEntry(ArrayList<Pair<ScreenSize, ScreenSize>> options, int slots, int width, int height) {
        int pages = MathHelper.ceil((double) slots / (width * height));
        int blanked = slots - pages * width * height;
        options.add(new Pair<>(ScreenSize.of(width, height), ScreenSize.of(pages, blanked)));
    }
}
