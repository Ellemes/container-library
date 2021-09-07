package ninjaphenix.container_library.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Should be implemented on blocks.
 */
public interface OpenableBlockEntityProvider {
    default List<OpenableBlockEntity> getParts(Level level, BlockState state, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof OpenableBlockEntity entity) {
            return List.of(entity);
        }
        return List.of();
    }
}
