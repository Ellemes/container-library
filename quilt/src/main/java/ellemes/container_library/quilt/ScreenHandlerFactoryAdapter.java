package ellemes.container_library.quilt;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ellemes.container_library.inventory.ServerScreenHandlerFactory;
import org.jetbrains.annotations.Nullable;

// Note: MenuProvider is important without it dependants will crash in development when opening inventories.
//       Yes java / mc modding is blessed.
public final class ScreenHandlerFactoryAdapter implements ExtendedScreenHandlerFactory, MenuProvider {
    private final ResourceLocation forcedScreenType;
    private final Component title;
    private final Container inventory;
    private final ServerScreenHandlerFactory factory;

    public ScreenHandlerFactoryAdapter(Component title, Container inventory, ServerScreenHandlerFactory factory, ResourceLocation forcedScreenType) {
        this.title = title;
        this.inventory = inventory;
        this.factory = factory;
        this.forcedScreenType = forcedScreenType;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
        buffer.writeInt(inventory.getContainerSize());
        if (forcedScreenType != null) {
            buffer.writeResourceLocation(forcedScreenType);
        }
    }

    @Override
    public Component getDisplayName() {
        return title;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return factory.create(syncId, inventory, playerInventory);
    }
}
