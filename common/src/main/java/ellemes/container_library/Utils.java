package ellemes.container_library;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ellemes.container_library.config.IdentifierTypeAdapter;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class Utils {
    public static final String MOD_ID = "ellemes_container_lib";
    // Gui Element Sizes
    public static final int SLOT_SIZE = 18;
    public static final int CONTAINER_HEADER_HEIGHT = 17;
    public static final int CONTAINER_PADDING_LDR = 7;
    // Handler Type ID
    public static final ResourceLocation HANDLER_TYPE_ID = Utils.id("handler_type");
    // Config Paths
    public static final String FABRIC_LEGACY_CONFIG_PATH = "ninjaphenix-container-library.json";
    public static final String FORGE_LEGACY_CONFIG_PATH = "expandedstorage-client.toml";
    public static final String CONFIG_PATH = "expandedstorage.json";
    // Config Related
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    // todo: look into possibility of replacing, might be worth exposing obj->json to configs.
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new IdentifierTypeAdapter())
                                                     .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                     .setPrettyPrinting()
                                                     .setLenient()
                                                     .create();
    private static final String LEGACY_MOD_ID = "expandedstorage";
    // Inbuilt Screen Types
    public static final ResourceLocation UNSET_SCREEN_TYPE = new ResourceLocation(LEGACY_MOD_ID, "auto");
    public static final ResourceLocation SCROLL_SCREEN_TYPE = new ResourceLocation(LEGACY_MOD_ID, "scroll");
    public static final ResourceLocation SINGLE_SCREEN_TYPE = new ResourceLocation(LEGACY_MOD_ID, "single");
    public static final ResourceLocation PAGE_SCREEN_TYPE = new ResourceLocation(LEGACY_MOD_ID, "page");
    // Key bind key
    public static final int KEY_BIND_KEY = GLFW.GLFW_KEY_G;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(Utils.MOD_ID, path);
    }

    public static MutableComponent translation(String key, Object... values) {
        return new TranslatableComponent(key, values);
    }
}
