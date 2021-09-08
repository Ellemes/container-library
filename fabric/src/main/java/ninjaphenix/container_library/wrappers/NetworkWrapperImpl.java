package ninjaphenix.container_library.wrappers;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.Minecraft;
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
import ninjaphenix.container_library.inventory.ServerMenuFactory;
import org.jetbrains.annotations.Nullable;

final class NetworkWrapperImpl extends NetworkWrapper {
    private static final ResourceLocation OPEN_INVENTORY = Utils.resloc("open_inventory");
    private static NetworkWrapperImpl INSTANCE;

    public static NetworkWrapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkWrapperImpl();
        }
        return INSTANCE;
    }

    public void initialise() {
        // Register Server Receivers
        ServerPlayConnectionEvents.INIT.register((listener_init, server_unused) -> {
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.OPEN_INVENTORY, this::s_handleOpenInventory);
        });
    }

    private void s_handleOpenInventory(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf buffer, PacketSender sender) {
        BlockPos pos = buffer.readBlockPos();
        server.execute(() -> this.openMenuIfAllowed(pos, player));
    }

    @Override
    public void c_openInventoryAt(BlockPos pos) {
        Client.openInventoryAt(pos);
    }

    @Override
    protected void openMenu(ServerPlayer player, BlockPos pos, Container container, ServerMenuFactory factory, Component title) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
                buffer.writeInt(container.getContainerSize());
            }

            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return factory.create(windowId, container, playerInventory);
            }
        });
    }

    private static class Client {
        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                Minecraft.getInstance().setScreen(new PickScreen(null, () -> Client.openInventoryAt(pos)));
            } else {
                if (ClientPlayNetworking.canSend(NetworkWrapperImpl.OPEN_INVENTORY)) {
                    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                    buffer.writeBlockPos(pos);
                    ClientPlayNetworking.send(NetworkWrapperImpl.OPEN_INVENTORY, buffer);
                }
            }
        }
    }
}
