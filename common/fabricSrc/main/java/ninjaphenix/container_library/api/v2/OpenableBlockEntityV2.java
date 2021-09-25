package ninjaphenix.container_library.api.v2;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;

public interface OpenableBlockEntityV2 {
    default boolean canBeUsedBy(ServerPlayerEntity player) {
        LockableContainerBlockEntity self = (LockableContainerBlockEntity) this;
        return self.getWorld().getBlockEntity(self.getPos()) == self && player.squaredDistanceTo(Vec3d.ofCenter(self.getPos())) <= 64 && self.checkUnlocked(player);
    }

    default Inventory getInventory() {
        return (Inventory) this;
    }

    default Text getInventoryTitle() {
        return ((Nameable) this).getDisplayName();
    }
}
