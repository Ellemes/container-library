package ninjaphenix.container_library;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class Utils {
    private static final String MOD_ID = "ninjaphenix-container-lib";
    // Expanded Storage Inventory Sizes
    public static final int WOOD_STACK_COUNT = 27;
    public static final int IRON_STACK_COUNT = 54;
    public static final int GOLD_STACK_COUNT = 81;
    public static final int DIAMOND_STACK_COUNT = 108;
    public static final int NETHERITE_STACK_COUNT = 135;
    // Gui Element Sizes
    public static final int SLOT_SIZE = 18;
    public static final int CONTAINER_HEADER_HEIGHT = 17;
    public static final int CONTAINER_PADDING_WIDTH = 7;
    // Inbuilt Screen Types
    public static final ResourceLocation UNSET_SCREEN_TYPE = Utils.resloc("auto");
    public static final ResourceLocation SCROLL_SCREEN_TYPE = Utils.resloc("scroll");
    public static final ResourceLocation SINGLE_SCREEN_TYPE = Utils.resloc("single");
    public static final ResourceLocation PAGE_SCREEN_TYPE = Utils.resloc("page");

    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(Utils.MOD_ID, path);
    }

    public static MutableComponent translation(String key) {
        return new TranslatableComponent(key);
    }
}
