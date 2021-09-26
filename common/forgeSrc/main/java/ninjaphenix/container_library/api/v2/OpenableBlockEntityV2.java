package ninjaphenix.container_library.api.v2;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public interface OpenableBlockEntityV2 {
    default boolean canBeUsedBy(ServerPlayerEntity player) {
        LockableLootTileEntity self = (LockableLootTileEntity) this;
        return self.getLevel().getBlockEntity(self.getBlockPos()) == self && player.distanceToSqr(Vector3d.atCenterOf(self.getBlockPos())) <= 64 && self.canOpen(player);
    }

    default IInventory getInventory() {
        return (IInventory) this;
    }

    default ITextComponent getInventoryTitle() {
        return ((INameable) this).getDisplayName();
    }
}
