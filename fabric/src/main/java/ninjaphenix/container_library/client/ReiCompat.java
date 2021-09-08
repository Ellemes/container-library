package ninjaphenix.container_library.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.client.renderer.Rect2i;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;

public class ReiCompat implements REIClientPlugin {
    private static Rectangle asReiRectangle(Rect2i rect) {
        return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractScreen.class, (AbstractScreen screen) -> {
            return CollectionUtils.map(screen.getExclusionZones(), ReiCompat::asReiRectangle);
        });
    }
}
