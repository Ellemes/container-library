package ninjaphenix.container_library.config;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;

import java.util.HashMap;
import java.util.Map;

public class ConfigV0 implements Config {
    private ResourceLocation screenType;
    private boolean restrictiveScrolling;
    private boolean preferBiggerScreens;

    public ConfigV0() {
        this(Utils.UNSET_SCREEN_TYPE, false, false);
    }

    public ConfigV0(ResourceLocation screenType, boolean restrictiveScrolling, boolean preferBiggerScreens) {
        this.screenType = screenType == null ? Utils.UNSET_SCREEN_TYPE : screenType;
        this.restrictiveScrolling = restrictiveScrolling;
        this.preferBiggerScreens = preferBiggerScreens;
    }

    public ResourceLocation getScreenType() {
        return screenType;
    }

    public void setScreenType(ResourceLocation screenType) {
        this.screenType = screenType;
    }

    public boolean isScrollingRestricted() {
        return restrictiveScrolling;
    }

    public void setScrollingRestricted(boolean scrollingRestricted) {
        this.restrictiveScrolling = scrollingRestricted;
    }

    public boolean preferBiggerScreens() {
        return this.preferBiggerScreens;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Converter<Map<String, Object>, ConfigV0> getConverter() {
        return Factory.INSTANCE;
    }

    public static final class Factory implements Converter<Map<String, Object>, ConfigV0> {
        public static final Factory INSTANCE = new Factory();

        private Factory() {

        }

        @Override
        public ConfigV0 fromSource(Map<String, Object> source) {
            if (source.get("container_type") instanceof String screenType && source.get("restrictive_scrolling") instanceof Boolean restrictiveScrolling) {
                Boolean preferBiggerScreens = Boolean.FALSE;
                if (source.containsKey("prefer_bigger_screens") && source.get("prefer_bigger_screens") instanceof Boolean bool) {
                    preferBiggerScreens = bool;
                }
                return new ConfigV0(ResourceLocation.tryParse(screenType), restrictiveScrolling, preferBiggerScreens);
            }
            return null;
        }

        @Override
        public Map<String, Object> toSource(ConfigV0 target) {
            Map<String, Object> values = new HashMap<>();
            values.put("container_type", target.screenType);
            values.put("restrictive_scrolling", target.restrictiveScrolling);
            values.put("prefer_bigger_screens", target.preferBiggerScreens);
            return values;
        }

        @Override
        public int getSourceVersion() {
            return 0;
        }

        @Override
        public int getTargetVersion() {
            return 0;
        }
    }
}
