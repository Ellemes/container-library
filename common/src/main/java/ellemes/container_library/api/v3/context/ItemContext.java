package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

// todo: expose slot id?
public class ItemContext implements Context {
    private final ServerLevel world;
    private final ServerPlayer player;
    private final ItemStack stack;

    public ItemContext(ServerLevel world, ServerPlayer player, ItemStack stack) {
        this.world = world;
        this.player = player;
        this.stack = stack;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public ServerLevel getWorld() {
        return world;
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }
}
