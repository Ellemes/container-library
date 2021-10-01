package ninjaphenix.container_library.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public final class ContainerLibraryJeiPlugin implements IModPlugin {
    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return Utils.id("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AbstractScreen.class, new IGuiContainerHandler<AbstractScreen>() {
            @Override
            public List<Rectangle2d> getGuiExtraAreas(AbstractScreen screen) {
                return screen.getExclusionZones();
            }
        });
    }
}
