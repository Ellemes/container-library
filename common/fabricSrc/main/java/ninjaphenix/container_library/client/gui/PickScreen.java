package ninjaphenix.container_library.client.gui;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
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
    private static final Map<ResourceLocation, PickButton> BUTTON_SETTINGS = new HashMap<>();
    private final Set<ResourceLocation> options;
    private final Supplier<Screen> returnToScreen;
    private final List<ScreenPickButton> optionWidgets;
    private final @Nullable Runnable onOptionPicked;
    private int topPadding;

    private record PickButton(ResourceLocation texture, Component title, ScreenSizePredicate warnTest,
                              List<Component> warningText) {

    }

    public PickScreen(Supplier<Screen> returnToScreen, @Nullable Runnable onOptionPicked) {
        super(new TranslatableComponent("screen.ninjaphenix_container_lib.screen_picker_title"));
        this.options = ImmutableSortedSet.copyOf(PickScreen.BUTTON_SETTINGS.keySet());
        this.optionWidgets = new ArrayList<>(options.size());
        this.onOptionPicked = onOptionPicked;
        this.returnToScreen = returnToScreen;
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareButtonSettings(ResourceLocation screenType, ResourceLocation texture, Component title, ScreenSizePredicate warnTest, List<Component> warningText) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(screenType, new PickButton(texture, title, warnTest, warningText));
    }

    @Override
    public void onClose() {
        //noinspection ConstantConditions
        minecraft.setScreen(returnToScreen.get());
    }

    @Override
    public boolean isPauseScreen() {
        //noinspection ConstantConditions
        return minecraft.level == null;
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation currentOption = ConfigWrapper.getInstance().getPreferredScreenType();
        int choices = options.size();
        int columns = Math.min(Mth.intFloorDiv(width, 96), choices);
        int innerPadding = Math.min((width - columns * 96) / (columns + 1), 20); // 20 is smallest gap for any screen.
        int outerPadding = (width - (((columns - 1) * innerPadding) + (columns * 96))) / 2;
        int x = 0;
        int topPadding = (height - 96) / 2;
        this.topPadding = topPadding;
        optionWidgets.clear();
        for (ResourceLocation option : options) {
            PickButton settings = PickScreen.BUTTON_SETTINGS.get(option);
            boolean isWarn = settings.warnTest().test(width, height);
            boolean isSelected = option.equals(currentOption);
            Button.OnTooltip tooltip = new Button.OnTooltip() {
                private static final Component CURRENT_OPTION_TEXT = Utils.translation("screen.ninjaphenix_container_lib.current_option_notice").withStyle(ChatFormatting.GOLD);

                @Override
                public void onTooltip(Button button, PoseStack stack, int x, int y) {
                    List<Component> tooltip = new ArrayList<>(4);
                    tooltip.add(button.getMessage());
                    if (isSelected) {
                        tooltip.add(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        tooltip.addAll(settings.warningText());
                    }
                    PickScreen.this.renderTooltip(stack, tooltip, Optional.empty(), x, y);
                }

                @Override
                public void narrateTooltip(Consumer<Component> consumer) {
                    if (isSelected) {
                        consumer.accept(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        var text = new TextComponent("");
                        for (Component component : settings.warningText()) {
                            text.append(component);
                        }
                        consumer.accept(text);
                    }
                }
            };
            optionWidgets.add(this.addRenderableWidget(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
                    settings.texture(), settings.title(), isWarn, option.equals(currentOption), button -> this.updatePlayerPreference(option), tooltip)));
            x++;
        }
    }

    private void updatePlayerPreference(ResourceLocation selection) {
        ConfigWrapper.getInstance().setPreferredScreenType(selection);
        if (onOptionPicked != null) {
            onOptionPicked.run();
        }
        this.onClose();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        optionWidgets.forEach(button -> button.renderTooltip(stack, mouseX, mouseY));
        GuiComponent.drawCenteredString(stack, font, title, width / 2, Math.max(topPadding / 2, 0), 0xFFFFFFFF);
    }
}
