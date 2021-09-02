package ninjaphenix.container_library.api.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.container_library.CommonMain;

public final class AbstractMenu extends AbstractContainerMenu {
    private final Container container;

    public AbstractMenu(int windowId, Container container, Inventory playerInventory) {
        super(CommonMain.getMenuType(), windowId);
        this.container = container;
        container.startOpen(playerInventory.player);
        // todo: set slot positions
    }

    //private static ResourceLocation getTexture(String type, int slotXCount, int slotYCount) {
    //    return Utils.resloc(String.format("textures/gui/container/%s_%d_%d.png", type, slotXCount, slotYCount));
    //}

    //private static <T extends ScreenMeta> T getNearestScreenMeta(int inventorySize, ImmutableMap<Integer, T> knownSizes) {
    //    T exactScreenMeta = knownSizes.get(inventorySize);
    //    if (exactScreenMeta != null) {
    //        return exactScreenMeta;
    //    }
    //    List<Integer> keys = knownSizes.keySet().asList();
    //    int index = Collections.binarySearch(keys, inventorySize);
    //    int largestKey = keys.get(Math.abs(index) - 1);
    //    T nearestMeta = knownSizes.get(largestKey);
    //    if (nearestMeta != null && largestKey > inventorySize && largestKey - inventorySize <= nearestMeta.width) {
    //        return nearestMeta;
    //    }
    //    throw new RuntimeException("No screen can show an inventory of size " + inventorySize + ".");
    //}

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public Container getInventory() {
        return container;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack newStack = slot.getItem();
            originalStack = newStack.copy();
            if (index < container.getContainerSize()) {
                if (!this.moveItemStackTo(newStack, container.getContainerSize(), container.getContainerSize() + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(newStack, 0, container.getContainerSize(), false)) {
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

    public static AbstractMenu createClientMenu(int windowId, Inventory inventory, FriendlyByteBuf buffer) {
        return new AbstractMenu(windowId, new SimpleContainer(buffer.readInt()), inventory);
    }

    public void resetSlotPositions(boolean createSlots) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            int slotXPos = i % screenMeta.width;
            int slotYPos = Mth.ceil((((double) (i - slotXPos)) / screenMeta.width));
            int realYPos = slotYPos >= screenMeta.height ? (Utils.SLOT_SIZE * (slotYPos % screenMeta.height)) - 2000 : slotYPos * Utils.SLOT_SIZE;
            if (createSlots) {
                this.addSlot(new Slot(container, i, slotXPos * Utils.SLOT_SIZE + 8, realYPos + Utils.SLOT_SIZE));
            } else {
                slots.get(i).y = realYPos + Utils.SLOT_SIZE;
            }
        }
    }

    public void moveSlotRange(int minSlotIndex, int maxSlotIndex, int yDifference) {
        for (int i = minSlotIndex; i < maxSlotIndex; i++) {
            slots.get(i).y += yDifference;
        }
    }

    public void setSlotRange(int minSlotIndex, int maxSlotIndex, IntUnaryOperator yMutator) {
        for (int i = minSlotIndex; i < maxSlotIndex; i++) {
            slots.get(i).y = yMutator.applyAsInt(i);
        }
    }
}
