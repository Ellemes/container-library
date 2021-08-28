package ninjaphenix.container_library.api.helpers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import ninjaphenix.container_library.api.OpenableBlockEntity;

import java.util.Arrays;

/**
 * Helper to wrap blocks which consist of multiple inventories into one e.g. chests.
 */
public final class OpenableBlockEntities implements OpenableBlockEntity {
    private final OpenableBlockEntity[] parts;
    private final Container inventory;
    private final Component inventoryName;

    public OpenableBlockEntities(Component inventoryName, OpenableBlockEntity... parts) {
        this.parts = parts;
        this.inventory = VariableInventory.of(Arrays.stream(parts).map(OpenableBlockEntity::getInventory).toArray(Container[]::new));
        this.inventoryName = inventoryName;
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
    public boolean canContinueUse(ServerPlayer player) {
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
    public Component getInventoryName() {
        return inventoryName;
    }
}
