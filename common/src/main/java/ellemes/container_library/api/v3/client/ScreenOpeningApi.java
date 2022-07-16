package ellemes.container_library.api.v3.client;

import ellemes.container_library.CommonClient;
import ellemes.container_library.CommonMain;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.api.v3.OpenableInventoryProvider;
import ellemes.container_library.client.gui.PickScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @apiNote Please use v2 api for now, not yet stable.
 */
@ApiStatus.Experimental
public class ScreenOpeningApi {
    private ScreenOpeningApi() {
        throw new IllegalStateException("ScreenOpeningApi should not be instantiated.");
    }

    /**
     * Open the config screen returning to the screen supplied.
     */
    public static void openTypeSelectScreen(@NotNull Supplier<Screen> returnToScreen) {
        Objects.requireNonNull(returnToScreen, "returnToScreen must not be null, pass () -> null instead.");
        Minecraft.getInstance().setScreen(new PickScreen(returnToScreen));
    }

    public static void openBlockInventory(@NotNull BlockPos pos) {
        Objects.requireNonNull(pos, "pos must not be null");
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Level world = player.getLevel();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof OpenableInventoryProvider<?> provider) {
                commonCode(provider, () -> CommonMain.getNetworkWrapper().c_openBlockInventory(pos));
                return;
            }
            throw new IllegalArgumentException("Block must be an OpenableInventoryProvider.");
        }
        throw new IllegalStateException("Not in world?");
    }

    public static void openEntityInventory(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        if (entity instanceof OpenableInventoryProvider<?> provider) {
            commonCode(provider, () -> CommonMain.getNetworkWrapper().c_openEntityInventory(entity));
            return;
        }
        throw new IllegalArgumentException("Entity must be an OpenableInventoryProvider.");
    }

//    public static void openItemInventory(int slotId) {
//        Player player = Minecraft.getInstance().player;
//        if (player != null) {
//            ItemStack stack = player.getInventory().getItem(slotId);
//            if (stack != ItemStack.EMPTY && stack.getItem() instanceof OpenableInventoryProvider<?> inventory) {
//                commonCode(inventory, () -> CommonMain.getNetworkWrapper().c_openItemInventory(slotId));
//                return;
//            }
//            throw new IllegalArgumentException("Item must be an OpenableInventoryProvider.");
//        }
//        throw new IllegalStateException("Not in world?");
//    }

    private static void commonCode(OpenableInventoryProvider<?> provider, Runnable runnable) {
        if (provider.getForcedScreenType() == null && !AbstractScreen.isScreenTypeDeclared(CommonClient.getConfigWrapper().getPreferredScreenType())) {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(new PickScreen(runnable));
            return;
        }
        runnable.run();
    }
}
