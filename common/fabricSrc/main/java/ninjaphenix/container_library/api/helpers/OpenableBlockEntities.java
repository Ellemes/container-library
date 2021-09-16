package ninjaphenix.container_library.api.helpers;

import ninjaphenix.container_library.api.OpenableBlockEntity;

import java.util.Arrays;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Helper to wrap blocks which consist of multiple inventories into one e.g. chests.
 */
public final class OpenableBlockEntities implements OpenableBlockEntity {
    private final OpenableBlockEntity[] parts;
    private final Inventory inventory;
    private final Text inventoryName;

    public OpenableBlockEntities(Text inventoryName, OpenableBlockEntity... parts) {
        this.parts = parts;
        this.inventory = VariableInventory.of(Arrays.stream(parts).map(OpenableBlockEntity::getInventory).toArray(Inventory[]::new));
        this.inventoryName = inventoryName;
    }

    @Override
    public boolean canBeUsedBy(ServerPlayerEntity player) {
        for (OpenableBlockEntity part : parts) {
            if (!part.canBeUsedBy(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canContinueUse(PlayerEntity player) {
        for (OpenableBlockEntity part : parts) {
            if (!part.canContinueUse(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Text getInventoryName() {
        return inventoryName;
    }
}
