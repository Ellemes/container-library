package ninjaphenix.container_library.api.client;

import net.minecraft.core.BlockPos;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

public interface NCL_ClientApi {
    /**
     * Call on client side to attempt to open an inventory.
     */
    default void openInventoryAt(BlockPos pos) {
        NetworkWrapper.getInstance().c_openInventoryAt(pos);
    }
}
