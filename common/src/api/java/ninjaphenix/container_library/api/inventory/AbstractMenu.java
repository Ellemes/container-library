package ninjaphenix.container_library.api.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.internal.api.inventory.screen.ScreenMeta;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

/**
 * Currently no way to use this with the api, will be added in the future.
 */
@ApiStatus.NonExtendable
public abstract class AbstractMenu<T extends ScreenMeta> extends AbstractContainerMenu {
    private final BlockPos pos;
    protected final T screenMeta;
    protected final Container container;

    public AbstractMenu(MenuType<?> menuType, int windowId, BlockPos pos, Container container,
                        Inventory playerInventory, T screenMeta) {
        super(menuType, windowId);
        this.pos = pos;
        this.container = container;
        this.screenMeta = screenMeta;
        container.startOpen(playerInventory.player);
    }

    public final BlockPos getPos() {
        return pos;
    }

    public final T getScreenMeta() {
        return screenMeta;
    }

    public static ResourceLocation getTexture(String type, int slotXCount, int slotYCount) {
        return Utils.resloc(String.format("textures/gui/container/%s_%d_%d.png", type, slotXCount, slotYCount));
    }

    protected static <T extends ScreenMeta> T getNearestScreenMeta(int inventorySize, ImmutableMap<Integer, T> knownSizes) {
        T exactScreenMeta = knownSizes.get(inventorySize);
        if (exactScreenMeta != null) {
            return exactScreenMeta;
        }
        List<Integer> keys = knownSizes.keySet().asList();
        int index = Collections.binarySearch(keys, inventorySize);
        int largestKey = keys.get(Math.abs(index) - 1);
        T nearestMeta = knownSizes.get(largestKey);
        if (nearestMeta != null && largestKey > inventorySize && largestKey - inventorySize <= nearestMeta.width) {
            return nearestMeta;
        }
        throw new RuntimeException("No screen can show an inventory of size " + inventorySize + ".");
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public final Container getInventory() {
        return container;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack newStack = slot.getItem();
            originalStack = newStack.copy();
            if (index < screenMeta.totalSlots) {
                if (!this.moveItemStackTo(newStack, screenMeta.totalSlots, screenMeta.totalSlots + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(newStack, 0, screenMeta.totalSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return originalStack;
    }
}
