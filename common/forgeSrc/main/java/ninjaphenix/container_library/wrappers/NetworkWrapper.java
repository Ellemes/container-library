package ninjaphenix.container_library.wrappers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import java.util.function.Consumer;

public abstract class NetworkWrapper {
    private static NetworkWrapper INSTANCE;

    protected abstract void openScreenHandler(ServerPlayerEntity player, IInventory inventory, ServerScreenHandlerFactory factory, ITextComponent title);

    public static NetworkWrapper getInstance() {
        if (NetworkWrapper.INSTANCE == null) {
            NetworkWrapper.INSTANCE = new NetworkWrapperImpl();
        }
        return NetworkWrapper.INSTANCE;
    }

    public final void s_openInventory(ServerPlayerEntity player, OpenableBlockEntityV2 inventory, Consumer<ServerPlayerEntity> onInitialOpen, BlockPos pos) {
        if (this.canOpenInventory(player, pos)) {
            ITextComponent title = inventory.getInventoryTitle();
            if (!inventory.canBeUsedBy(player)) {
                player.displayClientMessage(new TranslationTextComponent("container.isLocked", title), true);
                player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return;
            }
            onInitialOpen.accept(player);
            this.openScreenHandler(player, inventory.getInventory(), AbstractHandler::new, title);
        }
    }

    abstract boolean canOpenInventory(ServerPlayerEntity player, BlockPos pos);
}
