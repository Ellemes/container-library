package ninjaphenix.container_library.api.helpers;

import ninjaphenix.container_library.api.OpenableBlockEntity;

import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

/**
 * @deprecated Use V2 instead {@link ninjaphenix.container_library.api.v2.helpers.OpenableBlockEntitiesV2 }
 */
@Deprecated
public final class OpenableBlockEntities implements OpenableBlockEntity {
    private final OpenableBlockEntity[] parts;
    private final Container inventory;
    private final Component inventoryTitle;

    public OpenableBlockEntities(Component inventoryTitle, OpenableBlockEntity... parts) {
        this.parts = parts;
        this.inventory = VariableInventory.of(Arrays.stream(parts).map(OpenableBlockEntity::getInventory).toArray(Container[]::new));
        this.inventoryTitle = inventoryTitle;
    }

    @Override
    public boolean canBeUsedBy(ServerPlayer player) {
        for (OpenableBlockEntity part : parts) {
            if (!part.canBeUsedBy(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canContinueUse(Player player) {
        for (OpenableBlockEntity part : parts) {
            if (!part.canContinueUse(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Container getInventory() {
        return inventory;
    }

    @Override
    public Component getInventoryTitle() {
        return inventoryTitle;
    }
}
