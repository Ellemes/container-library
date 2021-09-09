package ninjaphenix.container_library.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import ninjaphenix.container_library.wrappers.NetworkWrapperImpl;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class OpenInventoryMessage {
    private final BlockPos pos;

    public OpenInventoryMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(OpenInventoryMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
    }

    public static OpenInventoryMessage decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        return new OpenInventoryMessage(pos);
    }

    public static void handle(OpenInventoryMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        context.enqueueWork(() -> NetworkWrapperImpl.getInstance().handleOpenInventory(message.pos, context.getSender()));
        context.setPacketHandled(true);
    }
}
