package ninjaphenix.container_library;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.config.IdentifierTypeAdapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class Utils {
    public static final String MOD_ID = "ninjaphenix_container_lib";
    // Gui Element Sizes
    public static final int SLOT_SIZE = 18;
    public static final int CONTAINER_HEADER_HEIGHT = 17;
    public static final int CONTAINER_PADDING_WIDTH = 7;
    // Menu Type ID
    public static final Identifier MENU_TYPE_ID = Utils.resloc("menu_type");
    // Config Paths
    public static final String FABRIC_LEGACY_CONFIG_PATH = "ninjaphenix-container-library.json";
    public static final String FORGE_LEGACY_CONFIG_PATH = "expandedstorage-client.toml";
    public static final String CONFIG_PATH = "expandedstorage.json";
    // Config Related
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    // todo: look into possibility of replacing with GsonHelper?
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
                                                     .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                     .setPrettyPrinting()
                                                     .setLenient()
                                                     .create();
    private static final String LEGACY_MOD_ID = "expandedstorage";
    // Inbuilt Screen Types
    public static final Identifier UNSET_SCREEN_TYPE = new Identifier(LEGACY_MOD_ID, "auto");
    public static final Identifier SCROLL_SCREEN_TYPE = new Identifier(LEGACY_MOD_ID, "scroll");
    public static final Identifier SINGLE_SCREEN_TYPE = new Identifier(LEGACY_MOD_ID, "single");
    public static final Identifier PAGE_SCREEN_TYPE = new Identifier(LEGACY_MOD_ID, "page");

    public static Identifier resloc(String path) {
        return new Identifier(Utils.MOD_ID, path);
    }

    public static MutableText translation(String key, Object... values) {
        return new TranslatableText(key, values);
    }

    public static <T> Set<T> sortedSetOf(Set<T> set) {
        return ImmutableSortedSet.copyOf(set);
    }
}
