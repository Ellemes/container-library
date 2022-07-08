package ellemes.container_library.api.v3;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

public interface OpenableInventory {
    /**
     * @return true if the inventory can be opened by the player,
     * e.g. if they are in range and the inventory owner is valid.
     */
    boolean canBeUsedBy(ServerPlayer player);

    /**
     * @return The inventory.
     */
    Container getInventory();

    /**
     * @return The text to display on top of the inventory.
     */
    Component getInventoryTitle();
}
