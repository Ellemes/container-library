package ninjaphenix.container_library.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;

public final class IdentifierTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter writer, ResourceLocation value) throws IOException {
        writer.value(value.toString());
    }

    @Override // never used.
    public ResourceLocation read(JsonReader reader) {
        return null;
    }
}
