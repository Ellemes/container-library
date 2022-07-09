package ellemes.container_library.fabric;

import ellemes.container_library.thread.wrappers.ThreadNetworkWrapper;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class FabricNetworkWrapper extends ThreadNetworkWrapper {
    public FabricNetworkWrapper(boolean flanPresent) {
        super(flanPresent);

        ServerPlayConnectionEvents.INIT.register((handler, __) -> {
            ServerPlayNetworking.registerReceiver(handler, ThreadNetworkWrapper.CHANNEL_NAME, (server, player, ____, buffer, _____) -> {
                buffer.retain();
                server.execute(() -> this.s_handleOpenInventory(player, buffer));
            });
        });
    }

    @Override
    public void c_openBlockInventory(BlockPos pos) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        this.writeBlockData(buffer, pos);
        ClientPlayNetworking.send(ThreadNetworkWrapper.CHANNEL_NAME, buffer);
    }

    @Override
    public void c_openEntityInventory(Entity entity) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        this.writeEntityData(buffer, entity);
        ClientPlayNetworking.send(ThreadNetworkWrapper.CHANNEL_NAME, buffer);
    }

//    @Override
//    public void c_openItemInventory(int slotId) {
//        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
//        this.writeItemData(buffer, slotId);
//        ClientPlayNetworking.send(ThreadNetworkWrapper.CHANNEL_NAME, buffer);
//    }
}
