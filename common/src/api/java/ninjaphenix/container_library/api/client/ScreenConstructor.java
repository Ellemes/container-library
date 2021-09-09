package ninjaphenix.container_library.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.inventory.AbstractMenu;
import org.jetbrains.annotations.ApiStatus;

public interface ScreenConstructor<T extends AbstractScreen> {
    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    ScreenConstructor<AbstractScreen> NULL = (menu, playerInventory, title) -> null;

    T createScreen(AbstractMenu menu, Inventory playerInventory, Component title);
}
