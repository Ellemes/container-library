package ninjaphenix.container_library.wrappers;

import net.minecraft.resources.ResourceLocation;

// todo: make abstract class
public interface ConfigWrapper {
    static ConfigWrapper getInstance() {
        return ConfigWrapperImpl.getInstance();
    }

    void initialise();

    boolean isScrollingUnrestricted();

    @SuppressWarnings("unused")
    void setScrollingRestricted(boolean value);

    ResourceLocation getPreferredScreenType();

    boolean setPreferredScreenType(ResourceLocation screenType);

    int getPreferredScreenWidth(int slots);

    int getPreferredScreenHeight(int slots);
}
