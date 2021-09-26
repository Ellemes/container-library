package ninjaphenix.container_library.config;

import net.minecraft.util.Identifier;
import ninjaphenix.container_library.Utils;

import java.util.HashMap;
import java.util.Map;

public class ConfigV0 implements Config {
    private Identifier screenType;
    private boolean restrictiveScrolling;
    private boolean preferSmallerScreens;

    public ConfigV0() {
        this(Utils.UNSET_SCREEN_TYPE, false, true);
    }

    public ConfigV0(Identifier screenType, boolean restrictiveScrolling, boolean preferSmallerScreens) {
        this.screenType = screenType == null ? Utils.UNSET_SCREEN_TYPE : screenType;
        this.restrictiveScrolling = restrictiveScrolling;
        this.preferSmallerScreens = preferSmallerScreens;
    }

    public Identifier getScreenType() {
        return screenType;
    }

    public void setScreenType(Identifier screenType) {
        this.screenType = screenType;
    }

    public boolean isScrollingRestricted() {
        return restrictiveScrolling;
    }

    public boolean preferSmallerScreens() {
        return this.preferSmallerScreens;
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
            Object screenType = source.get("container_type");
            Object restrictiveScrolling = source.get("restrictive_scrolling");
            if (screenType instanceof String && restrictiveScrolling instanceof Boolean) {
                Boolean preferSmallerScreens = Boolean.TRUE;
                Object bool = source.get("prefer_smaller_screens");
                if (source.containsKey("prefer_smaller_screens") && bool instanceof Boolean) {
                    preferSmallerScreens = (Boolean) bool;
                }
                return new ConfigV0(Identifier.tryParse((String) screenType), (Boolean) restrictiveScrolling, preferSmallerScreens);
            }
            return null;
        }

        @Override
        public Map<String, Object> toSource(ConfigV0 target) {
            Map<String, Object> values = new HashMap<>();
            values.put("container_type", target.screenType);
            values.put("restrictive_scrolling", target.restrictiveScrolling);
            values.put("prefer_bigger_screens", target.preferSmallerScreens);
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
