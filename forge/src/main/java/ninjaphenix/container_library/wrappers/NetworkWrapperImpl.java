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
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.inventory.ServerMenuFactory;
import ninjaphenix.container_library.network.OpenInventoryMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class NetworkWrapperImpl extends NetworkWrapper {
    private static NetworkWrapperImpl INSTANCE;
    private SimpleChannel channel;

    public static NetworkWrapperImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkWrapperImpl();
        }
        return INSTANCE;
    }

    public void initialise() {
        String channelVersion = "4";
        channel = NetworkRegistry.newSimpleChannel(Utils.resloc("channel"), () -> channelVersion, channelVersion::equals, channelVersion::equals);

        channel.registerMessage(0, OpenInventoryMessage.class, OpenInventoryMessage::encode, OpenInventoryMessage::decode, OpenInventoryMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public void c_openInventoryAt(BlockPos pos) {
        Client.openInventoryAt(pos);
    }

    @Override
    protected void openMenu(ServerPlayer player, BlockPos pos, Container container, ServerMenuFactory factory, Component displayName) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return factory.create(windowId, container, playerInventory);
            }
        }, buffer -> buffer.writeInt(container.getContainerSize()));
    }

    public void handleOpenInventory(BlockPos pos, ServerPlayer player) {
        this.openMenuIfAllowed(pos, player);
    }

    private static class Client {
        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                Minecraft.getInstance().setScreen(new PickScreen(null, () -> Client.openInventoryAt(pos)));
            } else {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                if (listener != null && NetworkWrapperImpl.INSTANCE.channel.isRemotePresent(listener.getConnection())) {
                    NetworkWrapperImpl.INSTANCE.channel.sendToServer(new OpenInventoryMessage(pos));
                }
            }
        }
    }
}
