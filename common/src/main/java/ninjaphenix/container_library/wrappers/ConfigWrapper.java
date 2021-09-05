package ninjaphenix.container_library.wrappers;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;

public abstract class ConfigWrapper {
    public static ConfigWrapper getInstance() {
        return ConfigWrapperImpl.getInstance();
    }

    public abstract void initialise();

    public abstract boolean isScrollingUnrestricted();

    @SuppressWarnings("unused")
    public abstract void setScrollingRestricted(boolean value);

    public abstract ResourceLocation getPreferredScreenType();

    public abstract boolean setPreferredScreenType(ResourceLocation screenType);

    public int getPreferredScreenWidth(int slots) {
        if (Utils.SINGLE_SCREEN_TYPE.equals(this.getPreferredScreenType())) {
            if (slots <= 81) {
                return 9;
            } else if (slots <= 108) {
                return 12;
            } else if (slots <= 135) {
                return 15;
            } else if (slots <= 270) {
                return 18;
            }
        } else {
            return 9;
        }
        throw new IllegalStateException("Cannot display single screen of size " + slots);
    }

    public int getPreferredScreenHeight(int slots) {
        if (Utils.SINGLE_SCREEN_TYPE.equals(this.getPreferredScreenType())) {
            if (slots <= 27) {
                return 3;
            } else if (slots <= 54) {
                return 6;
            } else if (slots <= 162) {
                return 9;
            } else if (slots <= 204) {
                return 12;
            } else if (slots <= 270) {
                return 15;
            }
        } else {
            return 6;
        }
        throw new IllegalStateException("Cannot display single screen of size " + slots);
    }
}
