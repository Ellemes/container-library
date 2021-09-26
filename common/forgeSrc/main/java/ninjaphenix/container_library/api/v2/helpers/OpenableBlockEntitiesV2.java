package ninjaphenix.container_library.api.v2.helpers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.ITextComponent;
import ninjaphenix.container_library.api.helpers.VariableInventory;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityV2;

import java.util.Arrays;

/**
 * Helper to wrap blocks which consist of multiple inventories into one e.g. chests.
 */
public final class OpenableBlockEntitiesV2 implements OpenableBlockEntityV2 {
    private final OpenableBlockEntityV2[] parts;
    private final IInventory inventory;
    private final ITextComponent inventoryTitle;

    public OpenableBlockEntitiesV2(ITextComponent inventoryTitle, OpenableBlockEntityV2... parts) {
        this.parts = parts;
        this.inventory = VariableInventory.of(Arrays.stream(parts).map(OpenableBlockEntityV2::getInventory).toArray(IInventory[]::new));
        this.inventoryTitle = inventoryTitle;
    }

    @Override
    public boolean canBeUsedBy(ServerPlayerEntity player) {
        for (OpenableBlockEntityV2 part : parts) {
            if (!part.canBeUsedBy(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public ITextComponent getInventoryTitle() {
        return inventoryTitle;
    }
}
