package ninjaphenix.container_library.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * @deprecated Use V2 instead {@link ninjaphenix.container_library.api.v2.OpenableBlockEntityV2 }
 */
@Deprecated
public interface OpenableBlockEntity {
    default boolean canBeUsedBy(ServerPlayer player) {
        return ((BaseContainerBlockEntity) this).canOpen(player);
    }

    default boolean canContinueUse(Player player) {
        BlockEntity self = (BlockEntity) this;
        return self.getLevel().getBlockEntity(self.getBlockPos()) == self && player.distanceToSqr(Vec3.atCenterOf(self.getBlockPos())) <= 64;
    }

    default Container getInventory() {
        return (Container) this;
    }

    default Component getInventoryTitle() {
        return ((Nameable) this).getDisplayName();
    }
}
