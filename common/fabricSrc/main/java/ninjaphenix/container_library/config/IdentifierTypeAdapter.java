package ninjaphenix.container_library.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public final class IdentifierTypeAdapter extends TypeAdapter<Identifier> {
    @Override
    public void write(JsonWriter writer, Identifier value) throws IOException {
        writer.value(value.toString());
    }

    @Override // never used.
    public Identifier read(JsonReader reader) {
        return null;
    }
}
