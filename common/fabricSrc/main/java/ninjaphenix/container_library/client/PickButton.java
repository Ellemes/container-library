package ninjaphenix.container_library.client;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public final class PickButton {
    private final Identifier texture;
    private final Text title;
    private final ScreenSizePredicate warningTest;
    private final List<Text> warningText;

    public PickButton(Identifier texture, Text title, ScreenSizePredicate warningTest, List<Text> warningText) {
        this.texture = texture;
        this.title = title;
        this.warningTest = warningTest;
        this.warningText = warningText;
    }

    public Identifier getTexture() {
        return texture;
    }

    public Text getTitle() {
        return title;
    }

    public ScreenSizePredicate getWarningTest() {
        return warningTest;
    }

    public List<Text> getWarningText() {
        return warningText;
    }
}
