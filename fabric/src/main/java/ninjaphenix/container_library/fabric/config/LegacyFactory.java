package ninjaphenix.container_library.fabric.config;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.config.ConfigV0;
import ninjaphenix.container_library.config.Converter;

import java.util.Map;

public final class LegacyFactory implements Converter<Map<String, Object>, ConfigV0> {
    public static final LegacyFactory INSTANCE = new LegacyFactory();

    private LegacyFactory() {

    }

    @Override
    public ConfigV0 fromSource(Map<String, Object> source) {
        if (source.get("preferred_container_type") instanceof String temp && source.get("restrictive_scrolling") instanceof Boolean restrictiveScrolling) {
            if ("expandedstorage:paged".equals(temp)) {
                temp = Utils.PAGE_SCREEN_TYPE.toString();
            } else if ("expandedstorage:scrollable".equals(temp)) {
                temp = Utils.SCROLL_SCREEN_TYPE.toString();
            }
            return new ConfigV0(ResourceLocation.tryParse(temp), restrictiveScrolling, true);
        }
        return null;
    }

    @Override
    public Map<String, Object> toSource(ConfigV0 target) {
        throw new UnsupportedOperationException("Legacy configs cannot be saved.");
    }

    @Override
    public int getSourceVersion() {
        return -1;
    }

    @Override
    public int getTargetVersion() {
        return 0;
    }
}
