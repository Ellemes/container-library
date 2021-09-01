package ninjaphenix.container_library.wrappers;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.internal.api.inventory.ServerMenuFactory;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

final class NetworkWrapperImpl extends NetworkWrapper {
    private static final ResourceLocation UPDATE_PLAYER_PREFERENCE = Utils.resloc("update_player_preference");
    private static final ResourceLocation OPEN_INVENTORY = Utils.resloc("open_inventory");
    private static final ResourceLocation NOTIFY_SERVER_MENU_TYPES = Utils.resloc("server_menu_types");
    private static NetworkWrapperImpl INSTANCE;

    public static NetworkWrapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkWrapperImpl();
        }
        return INSTANCE;
    }

    public void initialise() {
        if (PlatformUtils.getInstance().isClient()) {
            new Client().initialise();
        }
        // Register Server Receivers
        ServerPlayConnectionEvents.INIT.register((listener_init, server_unused) -> {
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, this::s_handleUpdatePlayerPreference);
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.OPEN_INVENTORY, this::s_handleOpenInventory);
        });
        ServerPlayConnectionEvents.JOIN.register(this::sendServerOptions);
        ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> this.s_setPlayerScreenType(listener.player, Utils.UNSET_SCREEN_TYPE));
    }

    private void sendServerOptions(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(menuFactories.size());
        menuFactories.keySet().forEach(buffer::writeResourceLocation);
        sender.sendPacket(NetworkWrapperImpl.NOTIFY_SERVER_MENU_TYPES, buffer);
    }

    private void s_handleOpenInventory(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf buffer, PacketSender sender) {
        var pos = buffer.readBlockPos();
        var preference = buffer.readableBytes() > 0 ? buffer.readResourceLocation() : null;
        server.execute(() -> {
            if (preference != null) {
                playerPreferences.put(player.getUUID(), preference);
            }
            this.openMenuIfAllowed(pos, player);
        });
    }

    private void s_handleUpdatePlayerPreference(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener,
                                                FriendlyByteBuf buffer, PacketSender sender) {
        ResourceLocation screenType = buffer.readResourceLocation();
        server.submit(() -> this.s_setPlayerScreenType(player, screenType));
    }

    @Override
    public void c2s_sendTypePreference(ResourceLocation selection) {
        if (ClientPlayNetworking.canSend(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE)) {
            ClientPlayNetworking.send(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(selection));
        }
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
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(pos).writeInt(container.getContainerSize());
            }

            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return factory.create(windowId, pos, container, playerInventory);
            }
        });
    }

    @Override
    public Set<ResourceLocation> getScreenOptions() {
        return Client.INSTANCE.screenOptions;
    }

    private class Client {
        private static Client INSTANCE;
        private Set<ResourceLocation> screenOptions = Utils.sortedSetOf(NetworkWrapperImpl.INSTANCE.menuFactories.keySet());

        private void initialise() {
            ClientPlayConnectionEvents.INIT.register((listener_init, client) -> ClientPlayNetworking.registerReceiver(NetworkWrapperImpl.NOTIFY_SERVER_MENU_TYPES, this::handleServerMenuTypes));
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> screenOptions = Utils.sortedSetOf(NetworkWrapperImpl.INSTANCE.menuFactories.keySet()));
            INSTANCE = this;
        }

        private void handleServerMenuTypes(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender sender) {
            int options = buffer.readInt();
            var serverOptions = new HashSet<ResourceLocation>();
            for (int i = 0; i < options; i++) {
                serverOptions.add(buffer.readResourceLocation());
            }
            serverOptions.removeIf(option -> !NetworkWrapperImpl.this.menuFactories.containsKey(option));
            client.submit(() -> {
                screenOptions = Utils.sortedSetOf(serverOptions);
                ResourceLocation option = ConfigWrapper.getInstance().getPreferredScreenType();
                if (screenOptions.contains(option)) {
                    sender.sendPacket(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(option));
                } else {
                    ConfigWrapper.getInstance().setPreferredScreenType(Utils.UNSET_SCREEN_TYPE);
                }
            });
        }

        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                Minecraft.getInstance().setScreen(new PickScreen(NetworkWrapper.getInstance().getScreenOptions(), null, (preference) -> Client.openInventoryAt(pos, preference)));
            } else {
                Client.openInventoryAt(pos, null);
            }
        }

        private static void openInventoryAt(BlockPos pos, @Nullable ResourceLocation preference) {
            if (ClientPlayNetworking.canSend(NetworkWrapperImpl.OPEN_INVENTORY)) {
                var buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeBlockPos(pos);
                if (preference != null) {
                    buffer.writeResourceLocation(preference);
                }
                ClientPlayNetworking.send(NetworkWrapperImpl.OPEN_INVENTORY, buffer);
            }
        }
    }
}
