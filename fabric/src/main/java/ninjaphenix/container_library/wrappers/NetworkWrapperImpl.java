package ninjaphenix.container_library.wrappers;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.inventory.ServerMenuFactory;
import org.jetbrains.annotations.Nullable;

final class NetworkWrapperImpl extends NetworkWrapper {
    private static final Identifier OPEN_INVENTORY = Utils.resloc("open_inventory");

    public void initialise() {
        // Register Server Receivers
        ServerPlayConnectionEvents.INIT.register((handler, server) -> ServerPlayNetworking.registerReceiver(handler, NetworkWrapperImpl.OPEN_INVENTORY, this::s_handleOpenInventory));
    }

    private void s_handleOpenInventory(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        BlockPos pos = buffer.readBlockPos();
        server.execute(() -> this.openMenuIfAllowed(pos, player));
    }

    @Override
    public void c_openInventoryAt(BlockPos pos) {
        Client.openInventoryAt(pos);
    }

    @Override
    protected void openMenu(ServerPlayerEntity player, BlockPos pos, Inventory inventory, ServerMenuFactory factory, Text title) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buffer) {
                buffer.writeInt(inventory.size());
            }

            @Override
            public Text getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        });
    }

    private static class Client {
        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                MinecraftClient.getInstance().setScreen(new PickScreen(null, () -> Client.openInventoryAt(pos)));
            } else {
                if (ClientPlayNetworking.canSend(NetworkWrapperImpl.OPEN_INVENTORY)) {
                    PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeBlockPos(pos);
                    ClientPlayNetworking.send(NetworkWrapperImpl.OPEN_INVENTORY, buffer);
                }
            }
        }
    }
}
