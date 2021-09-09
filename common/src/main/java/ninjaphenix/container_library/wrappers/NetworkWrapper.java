package ninjaphenix.container_library.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.container_library.api.OpenableBlockEntity;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.inventory.ServerMenuFactory;

public abstract class NetworkWrapper {
    public static NetworkWrapper getInstance() {
        return NetworkWrapperImpl.getInstance();
    }

    public abstract void initialise();

    public abstract void c_openInventoryAt(BlockPos pos);

    protected final void openMenuIfAllowed(BlockPos pos, ServerPlayer player) {
        ServerLevel world = player.getLevel();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof OpenableBlockEntityProvider block) {
            OpenableBlockEntity inventory = block.getOpenableBlockEntity(world, state, pos);
            if (inventory != null) {
                Component title = inventory.getInventoryName();
                if (player.containerMenu == null || player.containerMenu == player.inventoryMenu) {
                    if (inventory.canBeUsedBy(player)) {
                        block.onInitialOpen(player);
                    } else {
                        player.displayClientMessage(new TranslatableComponent("container.isLocked", title), true);
                        player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                    }
                }
                if (!inventory.canContinueUse(player)) {
                    return;
                }
                this.openMenu(player, pos, inventory.getInventory(), AbstractMenu::new, title);
            }
        }
    }

    protected abstract void openMenu(ServerPlayer player, BlockPos pos, Container inventory, ServerMenuFactory factory, Component title);
}
