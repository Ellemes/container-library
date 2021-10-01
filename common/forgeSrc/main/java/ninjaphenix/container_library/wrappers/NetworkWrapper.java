package ninjaphenix.container_library.wrappers;

import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;

public abstract class NetworkWrapper {
    private static NetworkWrapper INSTANCE;

    protected abstract void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title);

    public static NetworkWrapper getInstance() {
        if (NetworkWrapper.INSTANCE == null) {
            NetworkWrapper.INSTANCE = new NetworkWrapperImpl();
        }
        return NetworkWrapper.INSTANCE;
    }

    public final void s_openInventory(ServerPlayer player, OpenableBlockEntityV2 inventory, Consumer<ServerPlayer> onInitialOpen) {
        Component title = inventory.getInventoryTitle();
        if (!inventory.canBeUsedBy(player)) {
            player.displayClientMessage(new TranslatableComponent("container.isLocked", title), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        onInitialOpen.accept(player);
        this.openScreenHandler(player, inventory.getInventory(), AbstractHandler::new, title);
    }
}
