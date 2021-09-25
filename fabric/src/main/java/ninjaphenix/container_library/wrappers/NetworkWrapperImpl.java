package ninjaphenix.container_library.wrappers;

import draylar.goml.api.ClaimUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
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
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

final class NetworkWrapperImpl extends NetworkWrapper {
    private static final Identifier OPEN_INVENTORY = Utils.id("open_inventory");

    public void initialise() {
        // Register Server Receivers
        ServerPlayConnectionEvents.INIT.register((handler, server) -> ServerPlayNetworking.registerReceiver(handler, NetworkWrapperImpl.OPEN_INVENTORY, this::s_handleOpenInventory));
    }

    private void s_handleOpenInventory(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        BlockPos pos = buffer.readBlockPos();
        server.execute(() -> this.openScreenHandlerIfAllowed(pos, player));
    }

    @Override
    protected void openScreenHandler(ServerPlayerEntity player, Inventory inventory, ServerScreenHandlerFactory factory, Text title) {
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

    @Override
    protected boolean checkUsagePermission(ServerPlayerEntity player, BlockPos pos) {
        AtomicBoolean canUse = new AtomicBoolean(true);
        if (FabricLoader.getInstance().isModLoaded("goml")) {
            ClaimUtils.getClaimsAt(player.getServerWorld(), pos).forEach(claim -> {
                if (!claim.getValue().hasPermission(player)) {
                    canUse.set(false);
                }
            });
        }
        return canUse.get();
    }

    protected static class Client extends NetworkWrapper.Client {
        @Override
        void sendOpenInventoryPacket(BlockPos pos) {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBlockPos(pos);
            ClientPlayNetworking.send(NetworkWrapperImpl.OPEN_INVENTORY, buffer);
        }

        @Override
        boolean canSendOpenInventoryPacket() {
            return ClientPlayNetworking.canSend(NetworkWrapperImpl.OPEN_INVENTORY);
        }
    }
}
