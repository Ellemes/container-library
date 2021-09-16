package ninjaphenix.container_library.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;

public interface OpenableBlockEntity {
    default boolean canBeUsedBy(ServerPlayerEntity player) {
        return ((LockableContainerBlockEntity) this).checkUnlocked(player);
    }

    default boolean canContinueUse(PlayerEntity player) {
        BlockEntity self = (BlockEntity) this;
        return self.getWorld().getBlockEntity(self.getPos()) == self && player.squaredDistanceTo(Vec3d.ofCenter(self.getPos())) <= 64;
    }

    default Inventory getInventory() {
        return (Inventory) this;
    }

    default Text getInventoryName() {
        return ((Nameable) this).getDisplayName();
    }
}
