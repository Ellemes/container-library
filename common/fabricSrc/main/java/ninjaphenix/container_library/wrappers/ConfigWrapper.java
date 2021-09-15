package ninjaphenix.container_library.wrappers;

import net.minecraft.resources.ResourceLocation;

public abstract class ConfigWrapper {
    // todo: set config field here
    public static ConfigWrapper getInstance() {
        return ConfigWrapperImpl.getInstance();
    }

    public abstract void initialise();

    public abstract boolean isScrollingUnrestricted();

    public abstract ResourceLocation getPreferredScreenType();

    public abstract void setPreferredScreenType(ResourceLocation screenType);

    public abstract boolean preferBiggerScreens();
}
