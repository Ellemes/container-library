package ninjaphenix.container_library.client.gui;

import com.google.common.collect.ImmutableSortedSet;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.client.PickButton;
import ninjaphenix.container_library.client.gui.widget.ScreenPickButton;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PickScreen extends Screen {
    private static final Map<Identifier, PickButton> BUTTON_SETTINGS = new HashMap<>();
    private final Set<Identifier> options;
    private final Supplier<Screen> returnToScreen;
    private final List<ScreenPickButton> optionWidgets;
    private final @Nullable Runnable onOptionPicked;
    private int topPadding;

    public PickScreen(Supplier<Screen> returnToScreen, @Nullable Runnable onOptionPicked) {
        super(new TranslatableText("screen.ninjaphenix_container_lib.screen_picker_title"));
        this.options = ImmutableSortedSet.copyOf(PickScreen.BUTTON_SETTINGS.keySet());
        this.optionWidgets = new ArrayList<>(options.size());
        this.onOptionPicked = onOptionPicked;
        this.returnToScreen = returnToScreen;
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareButtonSettings(Identifier screenType, Identifier texture, Text title, ScreenSizePredicate warningTest, List<Text> warningText) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(screenType, new PickButton(texture, title, warningTest, warningText));
    }

    @Override
    public void onClose() {
        Screen screen = returnToScreen.get();
        if (screen == null) {
            client.player.closeHandledScreen();
        } else {
            client.setScreen(screen);
        }
    }

    @Override
    public boolean isPauseScreen() {
        //noinspection ConstantConditions
        return client.world == null;
    }

    @Override
    protected void init() {
        super.init();
        Identifier currentOption = ConfigWrapper.getInstance().getPreferredScreenType();
        int choices = options.size();
        int columns = Math.min(MathHelper.floorDiv(width, 96), choices);
        int innerPadding = Math.min((width - columns * 96) / (columns + 1), 20); // 20 is smallest gap for any screen.
        int outerPadding = (width - (((columns - 1) * innerPadding) + (columns * 96))) / 2;
        int x = 0;
        int topPadding = (height - 96) / 2;
        this.topPadding = topPadding;
        optionWidgets.clear();
        for (Identifier option : options) {
            PickButton settings = PickScreen.BUTTON_SETTINGS.get(option);
            boolean isWarn = settings.getWarningTest().test(width, height);
            boolean isSelected = option.equals(currentOption);
            ButtonWidget.TooltipSupplier tooltip = new ButtonWidget.TooltipSupplier() {
                private static final Text CURRENT_OPTION_TEXT = Utils.translation("screen.ninjaphenix_container_lib.current_option_notice").formatted(Formatting.GOLD);

                @Override
                public void onTooltip(ButtonWidget button, MatrixStack stack, int x, int y) {
                    List<Text> tooltip = new ArrayList<>(4);
                    tooltip.add(button.getMessage());
                    if (isSelected) {
                        tooltip.add(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        tooltip.addAll(settings.getWarningText());
                    }
                    PickScreen.this.renderTooltip(stack, tooltip, Optional.empty(), x, y);
                }

                @Override
                public void supply(Consumer<Text> consumer) {
                    if (isSelected) {
                        consumer.accept(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        MutableText text = new LiteralText("");
                        for (Text component : settings.getWarningText()) {
                            text.append(component);
                        }
                        consumer.accept(text);
                    }
                }
            };
            optionWidgets.add(this.addDrawableChild(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
                    settings.getTexture(), settings.getTitle(), isWarn, option.equals(currentOption), button -> this.updatePlayerPreference(option), tooltip)));
            x++;
        }
    }

    private void updatePlayerPreference(Identifier selection) {
        ConfigWrapper.getInstance().setPreferredScreenType(selection);
        if (onOptionPicked != null) {
            onOptionPicked.run();
        }
        this.onClose();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        optionWidgets.forEach(button -> button.renderButtonTooltip(stack, mouseX, mouseY));
        DrawableHelper.drawCenteredText(stack, textRenderer, title, width / 2, Math.max(topPadding / 2, 0), 0xFFFFFFFF);
    }
}
