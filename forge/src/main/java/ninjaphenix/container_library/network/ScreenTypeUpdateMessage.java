package ninjaphenix.container_library.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import ninjaphenix.container_library.wrappers.NetworkWrapper;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class ScreenTypeUpdateMessage {
    private final ResourceLocation screenType;

    public ScreenTypeUpdateMessage(ResourceLocation screenType) {
        this.screenType = screenType;
    }

    public static void encode(ScreenTypeUpdateMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.screenType);
    }

    public static ScreenTypeUpdateMessage decode(FriendlyByteBuf buffer) {
        ResourceLocation screenType = buffer.readResourceLocation();
        return new ScreenTypeUpdateMessage(screenType);
    }

    public static void handle(ScreenTypeUpdateMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            ResourceLocation screenType = message.screenType;
            context.enqueueWork(() -> NetworkWrapper.getInstance().s_setPlayerScreenType(player, screenType));
            context.setPacketHandled(true);
        }
    }
}
