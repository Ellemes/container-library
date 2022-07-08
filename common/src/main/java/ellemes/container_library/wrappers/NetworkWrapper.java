package ellemes.container_library.wrappers;

import ellemes.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.api.v2.OpenableBlockEntityV2;
import ellemes.container_library.inventory.ServerScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public abstract class NetworkWrapper {
    protected abstract void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType);

    public abstract void c_openBlockInventory(BlockPos pos);
    public abstract void c_openEntityInventory(Entity entity);
//    public abstract void c_openItemInventory(int slotId);
    public abstract void s_handleOpenInventory(FriendlyByteBuf buffer);

    public final void s_openInventory(ServerPlayer player, OpenableBlockEntityV2 inventory, Consumer<ServerPlayer> onInitialOpen, BlockPos pos, ResourceLocation forcedScreenType) {
        if (this.canOpenInventory(player, pos)) {
            Component title = inventory.getInventoryTitle();
            if (!inventory.canBeUsedBy(player)) {
                player.displayClientMessage(Component.translatable("container.isLocked", title), true);
                player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                return;
            }
            onInitialOpen.accept(player);
            this.openScreenHandler(player, inventory.getInventory(), (syncId, inv, playerInv) -> new AbstractHandler(syncId, inv, playerInv, null), title, forcedScreenType);
        }
    }

    public abstract boolean canOpenInventory(ServerPlayer player, BlockPos pos);
}
