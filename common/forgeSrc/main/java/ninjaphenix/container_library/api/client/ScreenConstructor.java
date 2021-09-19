package ninjaphenix.container_library.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import org.jetbrains.annotations.NotNull;

public interface ScreenConstructor<T extends AbstractScreen> {
    @NotNull
    T createScreen(@NotNull AbstractHandler handler, @NotNull Inventory playerInventory, @NotNull Component title, @NotNull ScreenSize screenSize);
}
