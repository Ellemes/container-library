package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityContext implements Context {
    private final ServerLevel world;
    private final ServerPlayer player;
    private final Entity entity;

    public EntityContext(ServerLevel world, ServerPlayer player, Entity entity) {
        this.world = world;
        this.player = player;
        this.entity = entity;
    }

    Entity getEntity() {
        return entity;
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
