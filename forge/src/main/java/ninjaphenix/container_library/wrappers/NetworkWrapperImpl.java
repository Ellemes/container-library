package ninjaphenix.container_library.wrappers;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import org.jetbrains.annotations.Nullable;

public final class NetworkWrapperImpl extends NetworkWrapper {
    @Override
    protected void openScreenHandler(ServerPlayerEntity player, IInventory inventory, ServerScreenHandlerFactory factory, ITextComponent title) {
        NetworkHooks.openGui(player, new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        }, buffer -> buffer.writeInt(inventory.getContainerSize()));
    }

    @Override
    boolean canOpenInventory(ServerPlayerEntity player, BlockPos pos) {
        boolean canOpenInventory = true;
        if (PlatformUtils.isModLoaded("flan")) {
            if (!ClaimHandler.getPermissionStorage(player.getLevel()).getForPermissionCheck(pos).canInteract(player, PermissionRegistry.OPENCONTAINER, pos, true)) {
                canOpenInventory = false;
            }
        }
        return canOpenInventory;
    }
}
