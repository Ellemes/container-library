package ellemes.container_library.api.v3.context;

import net.minecraft.world.entity.Entity;

public interface EntityContext extends Context {
    Entity getEntity();
}
