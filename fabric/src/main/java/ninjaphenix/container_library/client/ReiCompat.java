package ninjaphenix.container_library.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;

import java.util.ArrayList;
import java.util.List;

public class ReiCompat implements REIPluginV0 {
    private static Rectangle asReiRectangle(Rect2i rect) {
        return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        displayHelper.getBaseBoundsHandler().registerExclusionZones(AbstractScreen.class, () -> {
            List<Rectangle> rv = new ArrayList<>();
            for (Rect2i zone : ((AbstractScreen) MinecraftClient.getInstance().currentScreen).getExclusionZones()) {
                rv.add(ReiCompat.asReiRectangle(zone));
            }
            return rv;
        });
    }

    @Override
    public Identifier getPluginIdentifier() {
        return Utils.id("plugin");
    }
}
