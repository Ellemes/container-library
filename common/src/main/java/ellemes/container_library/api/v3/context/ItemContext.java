package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * @apiNote Please use v2 api for now, not yet stable.
 */
@ApiStatus.Experimental
public final class ItemContext extends BaseContext {
    private final ItemStack stack;

    // todo: expose slot id?
    public ItemContext(ServerLevel world, ServerPlayer player, ItemStack stack) {
        super(world, player);
        this.stack = stack;
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
