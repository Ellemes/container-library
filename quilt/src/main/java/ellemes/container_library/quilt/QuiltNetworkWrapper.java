package ellemes.container_library.quilt;

import ellemes.container_library.thread.wrappers.ThreadNetworkWrapper;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class QuiltNetworkWrapper extends ThreadNetworkWrapper {
    public QuiltNetworkWrapper(boolean flanPresent) {
        super(flanPresent);
        ServerPlayConnectionEvents.INIT.register((handler, __) -> {
            ServerPlayNetworking.registerReceiver(handler, ThreadNetworkWrapper.CHANNEL_NAME, (___, player, ____, buffer, _____) -> {
                this.s_handleOpenInventory(player, buffer);
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
