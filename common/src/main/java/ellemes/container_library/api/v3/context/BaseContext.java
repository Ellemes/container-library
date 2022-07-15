package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @apiNote Please use v2 api for now, not yet stable.
 */
@ApiStatus.Experimental
public class BaseContext {
    private final ServerLevel world;
    private final ServerPlayer player;

    public BaseContext(ServerLevel world, ServerPlayer player) {
        this.world = world;
        this.player = player;
    }

    public final ServerLevel getWorld() {
        return world;
    }

    public final ServerPlayer getPlayer() {
        return player;
    }
}
