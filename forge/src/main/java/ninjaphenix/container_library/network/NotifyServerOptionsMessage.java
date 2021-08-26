package ninjaphenix.container_library.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import ninjaphenix.container_library.wrappers.NetworkWrapperImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class NotifyServerOptionsMessage {
    private final Set<ResourceLocation> options;

    public NotifyServerOptionsMessage(Set<ResourceLocation> options) {
        this.options = options;
    }

    public static void encode(NotifyServerOptionsMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.options.size());
        for (ResourceLocation option : message.options) {
            buffer.writeResourceLocation(option);
        }
    }

    public static NotifyServerOptionsMessage decode(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        var options = new HashSet<ResourceLocation>();
        for (int i = 0; i < count; i++) {
            options.add(buffer.readResourceLocation());
        }
        return new NotifyServerOptionsMessage(options);
    }

    public static void handle(NotifyServerOptionsMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        context.enqueueWork(() -> NetworkWrapperImpl.getInstance().c_setServerOptions(message.options));
        context.setPacketHandled(true);
    }
}
