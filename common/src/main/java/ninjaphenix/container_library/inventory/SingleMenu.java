package ninjaphenix.container_library.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.container_library.CommonMain;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.inventory.screen.SingleScreenMeta;

public final class SingleMenu extends AbstractMenu<SingleScreenMeta> {
    // @formatter:off
    private static final ImmutableMap<Integer, SingleScreenMeta> SIZES = ImmutableMap.<Integer, SingleScreenMeta>builder()
            .put(Utils.WOOD_STACK_COUNT, new SingleScreenMeta(9, 3, Utils.WOOD_STACK_COUNT, AbstractMenu.getTexture("shared", 9, 3), 208, 192))
            .put(Utils.IRON_STACK_COUNT, new SingleScreenMeta(9, 6, Utils.IRON_STACK_COUNT, AbstractMenu.getTexture("shared", 9, 6), 208, 240))
            .put(Utils.GOLD_STACK_COUNT, new SingleScreenMeta(9, 9, Utils.GOLD_STACK_COUNT, AbstractMenu.getTexture("shared", 9, 9), 208, 304))
            .put(Utils.DIAMOND_STACK_COUNT, new SingleScreenMeta(12, 9, Utils.DIAMOND_STACK_COUNT, AbstractMenu.getTexture("shared", 12, 9), 256, 304))
            .put(Utils.NETHERITE_STACK_COUNT, new SingleScreenMeta(15, 9, Utils.NETHERITE_STACK_COUNT, AbstractMenu.getTexture("shared", 15, 9), 320, 304))
            .put(2 * Utils.GOLD_STACK_COUNT, new SingleScreenMeta(18, 9, 2 * Utils.GOLD_STACK_COUNT, AbstractMenu.getTexture("shared", 18, 9), 368, 304))
            .put(2 * Utils.DIAMOND_STACK_COUNT, new SingleScreenMeta(18, 12, 2 * Utils.DIAMOND_STACK_COUNT, AbstractMenu.getTexture("shared", 18, 12), 368, 352))
            .put(2 * Utils.NETHERITE_STACK_COUNT, new SingleScreenMeta(18, 15, 2 * Utils.NETHERITE_STACK_COUNT, AbstractMenu.getTexture("shared", 18, 15), 368, 416))
            .build();
    // @formatter:on

    public SingleMenu(int windowId, BlockPos pos, Container container, Inventory playerInventory) {
        super(CommonMain.getSingleMenuType(), windowId, pos, container, playerInventory,
                AbstractMenu.getNearestScreenMeta(container.getContainerSize(), SingleMenu.SIZES));
        for (int i = 0; i < container.getContainerSize(); i++) {
            int x = i % screenMeta.width;
            int y = (i - x) / screenMeta.width;
            this.addSlot(new Slot(container, i, x * Utils.SLOT_SIZE + 8, y * Utils.SLOT_SIZE + Utils.SLOT_SIZE));
        }
        int left = (screenMeta.width * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (screenMeta.height * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    public static SingleMenu createClientMenu(int windowId, Inventory inventory, FriendlyByteBuf buffer) {
        if (buffer == null) {
            return null;
        }
        return new SingleMenu(windowId, buffer.readBlockPos(), new SimpleContainer(buffer.readInt()), inventory);
    }
}
