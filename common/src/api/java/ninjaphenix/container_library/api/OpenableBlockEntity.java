package ninjaphenix.container_library.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

public interface OpenableBlockEntity {
    default boolean canBeUsedBy(ServerPlayer player) {
        return ((BaseContainerBlockEntity) this).canOpen(player);
    }

    default Container getInventory() {
        return (Container) this;
    }

    default boolean hasCustomInventoryName() {
        return ((Nameable) this).hasCustomName();
    }

    default Component getInventoryName() {
        return ((Nameable) this).getDisplayName();
    }
}
