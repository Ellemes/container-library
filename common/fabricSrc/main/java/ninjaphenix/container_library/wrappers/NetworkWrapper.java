package ninjaphenix.container_library.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.container_library.api.OpenableBlockEntity;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import ninjaphenix.container_library.inventory.ServerMenuFactory;

public abstract class NetworkWrapper {
    private static NetworkWrapper INSTANCE;

    public static NetworkWrapper getInstance() {
        if (NetworkWrapper.INSTANCE == null) {
            NetworkWrapper.INSTANCE = new NetworkWrapperImpl();
        }
        return NetworkWrapper.INSTANCE;
    }

    public abstract void initialise();

    public abstract void c_openInventoryAt(BlockPos pos);

    protected final void openMenuIfAllowed(BlockPos pos, ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof OpenableBlockEntityProvider block) {
            OpenableBlockEntity inventory = block.getOpenableBlockEntity(world, state, pos);
            if (inventory != null) {
                Text title = inventory.getInventoryName();
                if (player.currentScreenHandler == null || player.currentScreenHandler == player.playerScreenHandler) {
                    if (inventory.canBeUsedBy(player)) {
                        block.onInitialOpen(player);
                    } else {
                        player.sendMessage(new TranslatableText("container.isLocked", title), true);
                        player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
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

    protected abstract void openMenu(ServerPlayerEntity player, BlockPos pos, Inventory inventory, ServerMenuFactory factory, Text title);

    public final NetworkWrapperImpl toInternal() {
        return (NetworkWrapperImpl) this;
    }
}
