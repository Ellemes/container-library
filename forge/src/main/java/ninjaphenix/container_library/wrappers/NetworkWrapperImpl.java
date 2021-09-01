package ninjaphenix.container_library.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.internal.api.inventory.ServerMenuFactory;
import ninjaphenix.container_library.network.NotifyServerOptionsMessage;
import ninjaphenix.container_library.network.OpenInventoryMessage;
import ninjaphenix.container_library.network.ScreenTypeUpdateMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class NetworkWrapperImpl extends NetworkWrapper {
    private static NetworkWrapperImpl INSTANCE;
    private SimpleChannel channel;

    public static NetworkWrapperImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkWrapperImpl();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public static void sOnPlayerConnected(PlayerEvent.PlayerLoggedInEvent event) {
        NetworkWrapperImpl.INSTANCE.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new NotifyServerOptionsMessage(NetworkWrapperImpl.INSTANCE.menuFactories.keySet()));
    }

    @SubscribeEvent
    public static void sOnPlayerDisconnected(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) { // Probably called on both sides.
            NetworkWrapper.getInstance().s_setPlayerScreenType(player, Utils.UNSET_SCREEN_TYPE);
        }
    }

    public void initialise() {
        String channelVersion = "3";
        channel = NetworkRegistry.newSimpleChannel(Utils.resloc("channel"), () -> channelVersion, channelVersion::equals, channelVersion::equals);

        channel.registerMessage(0, ScreenTypeUpdateMessage.class, ScreenTypeUpdateMessage::encode, ScreenTypeUpdateMessage::decode, ScreenTypeUpdateMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(1, NotifyServerOptionsMessage.class, NotifyServerOptionsMessage::encode, NotifyServerOptionsMessage::decode, NotifyServerOptionsMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        channel.registerMessage(2, OpenInventoryMessage.class, OpenInventoryMessage::encode, OpenInventoryMessage::decode, OpenInventoryMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        if (PlatformUtils.getInstance().isClient()) {
            new Client().initialize();
        }

        MinecraftForge.EVENT_BUS.addListener(NetworkWrapperImpl::sOnPlayerConnected);
        MinecraftForge.EVENT_BUS.addListener(NetworkWrapperImpl::sOnPlayerDisconnected);
    }

    @Override
    public void c2s_sendTypePreference(ResourceLocation selection) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener != null && channel.isRemotePresent(listener.getConnection())) {
            channel.sendToServer(new ScreenTypeUpdateMessage(selection));
        }
    }

    @Override
    public Set<ResourceLocation> getScreenOptions() {
        return Client.screenOptions;
    }

    @Override
    public void c_openInventoryAt(BlockPos pos) {
        Client.openInventoryAt(pos);
    }

    @Override
    public void c_openInventoryAt(BlockPos pos, ResourceLocation selection) {
        Client.openInventoryAt(pos, selection);
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
                return factory.create(windowId, pos, container, playerInventory);
            }
        }, buffer -> buffer.writeBlockPos(pos).writeInt(container.getContainerSize()));
    }

    public void c_setServerOptions(Set<ResourceLocation> options) {
        options.removeIf(option -> !menuFactories.containsKey(option));
        Client.screenOptions = Utils.sortedSetOf(options);
        ResourceLocation option = ConfigWrapper.getInstance().getPreferredScreenType();
        if (options.contains(option)) {
            channel.sendToServer(new ScreenTypeUpdateMessage(option));
        } else {
            ConfigWrapper.getInstance().setPreferredScreenType(Utils.UNSET_SCREEN_TYPE);
        }
    }

    public void handleOpenInventory(BlockPos pos, ServerPlayer player, ResourceLocation preference) {
        if (preference != null) {
            playerPreferences.put(player.getUUID(), preference);
        }
        this.openMenuIfAllowed(pos, player);
    }

    private static class Client {
        private static Set<ResourceLocation> screenOptions = Utils.sortedSetOf(NetworkWrapperImpl.INSTANCE.menuFactories.keySet());

        private void initialize() {
            MinecraftForge.EVENT_BUS.addListener(Client::cOnPlayerDisconnected);
        }

        @SubscribeEvent
        public static void cOnPlayerDisconnected(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            Client.screenOptions = Utils.sortedSetOf(NetworkWrapperImpl.INSTANCE.menuFactories.keySet());
        }

        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                Minecraft.getInstance().setScreen(new PickScreen(NetworkWrapper.getInstance().getScreenOptions(), null, (preference) -> Client.openInventoryAt(pos, preference)));
            } else {
                Client.openInventoryAt(pos, null);
            }
        }

        private static void openInventoryAt(BlockPos pos, @Nullable ResourceLocation preference) {
            ClientPacketListener listener = Minecraft.getInstance().getConnection();
            if (listener != null && NetworkWrapperImpl.INSTANCE.channel.isRemotePresent(listener.getConnection())) {
                NetworkWrapperImpl.INSTANCE.channel.sendToServer(new OpenInventoryMessage(pos, preference));
            }
        }
    }
}
