package ellemes.container_library.api.v3;

import ellemes.container_library.api.v3.context.Context;
import ellemes.container_library.api.v3.helpers.OpenableInventories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Can be implemented on blocks, entities, or <strike>items</strike>.
 */
public interface OpenableInventoryProvider<T extends Context> {
    /**
     * Return the openable inventory, {@link OpenableInventories} can be used to supply more than one inventory.
     */
    OpenableInventory getOpenableInventory(T context);

    /**
     * Call back for running code when an inventory is initially opened, can be used to award opening stats.
     * Note: more context can be provided if needed, namely ServerWorld.
     */
    default void onInitialOpen(ServerPlayer player) {

    }

    /**
     * @return The screen type that should be used, null if the player can decide.
     */
    default ResourceLocation getForcedScreenType() {
        return null;
    }
}
