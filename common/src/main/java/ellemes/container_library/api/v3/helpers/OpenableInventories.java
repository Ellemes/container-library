package ellemes.container_library.api.v3.helpers;

import ellemes.container_library.api.helpers.VariableInventory;
import ellemes.container_library.api.v3.OpenableInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

import java.util.Arrays;

public class OpenableInventories implements OpenableInventory {
    private final OpenableInventory[] parts;
    private final Container inventory;
    private final Component inventoryTitle;
    private final ResourceLocation forcedScreenType;

    private OpenableInventories(Component inventoryTitle, ResourceLocation forcedScreenType, OpenableInventory... parts) {
        this.parts = parts;
        this.inventory = VariableInventory.of(Arrays.stream(parts).map(OpenableInventory::getInventory).toArray(Container[]::new));
        this.inventoryTitle = inventoryTitle;
        this.forcedScreenType = forcedScreenType;
    }

    public static OpenableInventory of(Component inventoryTitle, ResourceLocation forcedScreenType, OpenableInventory... parts) {
        return new OpenableInventories(inventoryTitle, forcedScreenType, parts);
    }

    @Override
    public boolean canBeUsedBy(ServerPlayer player) {
        for (OpenableInventory part : parts) {
            if (!part.canBeUsedBy(player)) {
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

    @Override
    public ResourceLocation getForcedScreenType() {
        return forcedScreenType;
    }
}
