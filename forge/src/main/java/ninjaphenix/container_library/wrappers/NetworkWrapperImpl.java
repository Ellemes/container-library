package ninjaphenix.container_library.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import ninjaphenix.container_library.network.OpenInventoryMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class NetworkWrapperImpl extends NetworkWrapper {
    private SimpleChannel channel;

    public void initialise() {
        String channelVersion = "1";
        channel = NetworkRegistry.newSimpleChannel(Utils.id("channel"), () -> channelVersion, channelVersion::equals, channelVersion::equals);

        channel.registerMessage(0, OpenInventoryMessage.class, OpenInventoryMessage::encode, OpenInventoryMessage::decode, OpenInventoryMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    @Override
    protected void openScreenHandler(ServerPlayer player, BlockPos pos, Container inventory, ServerScreenHandlerFactory factory, Component title) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        }, buffer -> buffer.writeInt(inventory.getContainerSize()));
    }

    @Override
    protected boolean checkUsagePermission(ServerPlayer player, BlockPos pos) {
        return true;
    }

    public void handleOpenInventory(BlockPos pos, ServerPlayer player) {
        this.openScreenHandlerIfAllowed(pos, player);
    }

    protected static class Client extends NetworkWrapper.Client {
        @Override
        void sendOpenInventoryPacket(BlockPos pos) {
            NetworkWrapper.getInstance().toInternal().channel.sendToServer(new OpenInventoryMessage(pos));
        }

        @Override
        boolean canSendOpenInventoryPacket() {
            ClientPacketListener handler = Minecraft.getInstance().getConnection();
            return handler != null && NetworkWrapper.getInstance().toInternal().channel.isRemotePresent(handler.getConnection());
        }
    }
}
