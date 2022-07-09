package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
