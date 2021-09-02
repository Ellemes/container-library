package ninjaphenix.container_library.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.internal.api.client.gui.AbstractScreen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ContainerLibraryJeiPlugin implements IModPlugin {
    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return Utils.resloc("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AbstractScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(AbstractScreen screen) {
                return screen.getExclusionZones();
            }
        });
    }
}
