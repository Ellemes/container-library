package ellemes.container_library.api.v3;

import ellemes.container_library.api.v3.context.BaseContext;
import ellemes.container_library.api.v3.helpers.OpenableInventories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Can be implemented on blocks, entities, or <strike>items</strike>.
 *
 * @apiNote Please use v2 api for now, not yet stable.
 */
@ApiStatus.Experimental
public interface OpenableInventoryProvider<T extends BaseContext> {
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
