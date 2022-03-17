package ninjaphenix.container_library.wrappers;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkWrapper {
    private void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType) {
        MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buffer) {
                buffer.writeInt(inventory.getContainerSize());
                if (forcedScreenType != null) {
                    buffer.writeResourceLocation(forcedScreenType);
                }
            }

            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        });
    }

    public final void s_openInventory(ServerPlayer player, OpenableBlockEntityV2 inventory, Consumer<ServerPlayer> onInitialOpen, BlockPos pos, ResourceLocation forcedScreenType) {
        if (this.canOpenInventory(player, pos)) {
            Component title = inventory.getInventoryTitle();
            if (!inventory.canBeUsedBy(player)) {
                player.displayClientMessage(new TranslatableComponent("container.isLocked", title), true);
                player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                return;
            }
            onInitialOpen.accept(player);
            this.openScreenHandler(player, inventory.getInventory(), (syncId, inv, playerInv) -> new AbstractHandler(syncId, inv, playerInv, null), title, forcedScreenType);
        }
    }

    public abstract boolean canOpenInventory(ServerPlayer player, BlockPos pos);
}
