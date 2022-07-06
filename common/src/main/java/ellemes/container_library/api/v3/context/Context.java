package ellemes.container_library.api.v3.context;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface Context {
    Level getWorld();

    Player getPlayer();
}
