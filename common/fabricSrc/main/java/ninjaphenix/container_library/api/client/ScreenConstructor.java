package ninjaphenix.container_library.api.client;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import ninjaphenix.container_library.api.client.function.ScreenSize;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import org.jetbrains.annotations.NotNull;

public interface ScreenConstructor<T extends AbstractScreen> {
    @NotNull
    T createScreen(@NotNull AbstractHandler handler, @NotNull PlayerInventory playerInventory, @NotNull Text title, @NotNull ScreenSize screenSize);
}
