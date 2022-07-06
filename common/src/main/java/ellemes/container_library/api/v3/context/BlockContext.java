package ellemes.container_library.api.v3.context;

import net.minecraft.core.BlockPos;

public interface BlockContext extends Context {
    BlockPos getBlockPos();
}
