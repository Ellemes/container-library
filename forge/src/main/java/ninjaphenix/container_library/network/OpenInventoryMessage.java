package ninjaphenix.container_library.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import ninjaphenix.container_library.wrappers.NetworkWrapperImpl;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class OpenInventoryMessage {
    private final BlockPos pos;
    @Nullable
    private final ResourceLocation preference;

    public OpenInventoryMessage(BlockPos pos, @Nullable ResourceLocation preference) {
        this.pos = pos;
        this.preference = preference;
    }

    public static void encode(OpenInventoryMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        if (message.preference != null) {
            buffer.writeResourceLocation(message.preference);
        }
    }

    public static OpenInventoryMessage decode(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        ResourceLocation preference = null;
        if (buffer.readableBytes() > 0) {
            preference = buffer.readResourceLocation();
        }
        return new OpenInventoryMessage(pos, preference);
    }

    public static void handle(OpenInventoryMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        context.enqueueWork(() -> NetworkWrapperImpl.getInstance().handleOpenInventory(message.pos, context.getSender(), message.preference));
        context.setPacketHandled(true);
    }
}
