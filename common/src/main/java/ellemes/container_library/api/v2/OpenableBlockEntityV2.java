package ellemes.container_library.api.v2;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.phys.Vec3;

public interface OpenableBlockEntityV2 {
    default boolean canBeUsedBy(ServerPlayer player) {
        BaseContainerBlockEntity self = (BaseContainerBlockEntity) this;
        return self.getLevel().getBlockEntity(self.getBlockPos()) == self && player.distanceToSqr(Vec3.atCenterOf(self.getBlockPos())) <= 64 && self.canOpen(player);
    }

    default Container getInventory() {
        return (Container) this;
    }

    default Component getInventoryTitle() {
        return ((Nameable) this).getDisplayName();
    }
}
