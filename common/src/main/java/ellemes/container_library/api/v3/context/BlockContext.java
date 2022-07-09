package ellemes.container_library.api.v3.context;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BlockContext implements Context {
    private final ServerLevel world;
    private final ServerPlayer player;
    private final BlockPos pos;

    public BlockContext(ServerLevel world, ServerPlayer player, BlockPos pos) {
        this.world = world;
        this.player = player;
        this.pos = pos;
    }

    BlockPos getBlockPos() {
        return pos;
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
