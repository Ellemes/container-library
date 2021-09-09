package ninjaphenix.container_library.wrappers;

import net.minecraft.resources.ResourceLocation;

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
}
