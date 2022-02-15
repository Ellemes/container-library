package ninjaphenix.container_library.wrappers;

import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;

import java.util.function.Consumer;

public abstract class NetworkWrapper {
    private static NetworkWrapper INSTANCE;

    protected abstract void openScreenHandler(ServerPlayerEntity player, Inventory inventory, ServerScreenHandlerFactory factory, Text title, Identifier forcedScreenType);

    public static NetworkWrapper getInstance() {
        if (NetworkWrapper.INSTANCE == null) {
            NetworkWrapper.INSTANCE = new NetworkWrapperImpl();
        }
        return NetworkWrapper.INSTANCE;
    }

    public final void s_openInventory(ServerPlayerEntity player, OpenableBlockEntityV2 inventory, Consumer<ServerPlayerEntity> onInitialOpen, BlockPos pos, Identifier forcedScreenType) {
        if (this.canOpenInventory(player, pos)) {
            Text title = inventory.getInventoryTitle();
            if (!inventory.canBeUsedBy(player)) {
                player.sendMessage(new TranslatableText("container.isLocked", title), true);
                player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return;
            }
            onInitialOpen.accept(player);
            this.openScreenHandler(player, inventory.getInventory(), (syncId, inv, playerInv) -> new AbstractHandler(syncId, inv, playerInv, null), title, forcedScreenType);
        }
    }

    public abstract boolean canOpenInventory(ServerPlayerEntity player, BlockPos pos);
}
