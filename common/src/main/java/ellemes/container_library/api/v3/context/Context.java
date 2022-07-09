package ellemes.container_library.api.v3.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface Context {
    ServerLevel getWorld();

    ServerPlayer getPlayer();
}
