package ellemes.container_library.forge;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

public class OpenInventoryMessage {
    private Consumer<FriendlyByteBuf> dataWriter;
    private FriendlyByteBuf data;

    public OpenInventoryMessage(Consumer<FriendlyByteBuf> dataWriter) {
        this.dataWriter = dataWriter;
    }

    public OpenInventoryMessage(FriendlyByteBuf data) {
        this.data = data;
    }

    public static void encode(OpenInventoryMessage message, FriendlyByteBuf buffer) {
        message.dataWriter.accept(buffer);
    }

    public static OpenInventoryMessage decode(FriendlyByteBuf buffer) {
        buffer.retain();
        return new OpenInventoryMessage(buffer);
    }

    public FriendlyByteBuf getData() {
        return data;
    }
}
