package ninjaphenix.container_library.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;

import java.util.List;

public final class PickButton {
    private final ResourceLocation texture;
    private final ITextComponent title;
    private final ScreenSizePredicate warningTest;
    private final List<ITextComponent> warningText;

    public PickButton(ResourceLocation texture, ITextComponent title, ScreenSizePredicate warningTest, List<ITextComponent> warningText) {
        this.texture = texture;
        this.title = title;
        this.warningTest = warningTest;
        this.warningText = warningText;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ITextComponent getTitle() {
        return title;
    }

    public ScreenSizePredicate getWarningTest() {
        return warningTest;
    }

    public List<ITextComponent> getWarningText() {
        return warningText;
    }
}
