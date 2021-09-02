package ninjaphenix.container_library.client.gui;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.internal.api.client.gui.widget.ScreenPickButton;
import ninjaphenix.container_library.internal.api.function.ScreenSizePredicate;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class PickScreen extends Screen {
    private static final Map<ResourceLocation, Triple<ResourceLocation, Component, ScreenSizePredicate>> BUTTON_SETTINGS = new HashMap<>();
    private final Set<ResourceLocation> options;
    private final Screen returnToScreen;
    private final List<ScreenPickButton> optionWidgets;
    private final @Nullable Runnable onOptionPicked;
    private int topPadding;

    public PickScreen(Screen returnToScreen, @Nullable Runnable onOptionPicked) {
        super(new TranslatableComponent("screen.ninjaphenix_container_lib.screen_picker_title"));
        this.options = ImmutableSortedSet.copyOf(PickScreen.BUTTON_SETTINGS.keySet());
        this.optionWidgets = new ArrayList<>(options.size());
        this.onOptionPicked = onOptionPicked;
        this.returnToScreen = returnToScreen;
    }

    public static void declareButtonSettings(ResourceLocation screenType, ResourceLocation texture, Component text, ScreenSizePredicate warnTest) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(screenType, Triple.of(texture, text, warnTest));
    }

    @Override
    public void onClose() {
        //noinspection ConstantConditions
        minecraft.setScreen(returnToScreen);
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
            Triple<ResourceLocation, Component, ScreenSizePredicate> settings = PickScreen.BUTTON_SETTINGS.get(option);
            boolean isWarn = settings.getRight().test(width, height);
            boolean isSelected = option.equals(currentOption);
            Button.OnTooltip tooltip = new Button.OnTooltip() {
                private static final MutableComponent WARN_TEXT_1 = Utils.translation("screen.ninjaphenix_container_lib.off_screen_warning_1").withStyle(ChatFormatting.GRAY);
                private static final Component WARN_TEXT_2 = Utils.translation("screen.ninjaphenix_container_lib.off_screen_warning_2").withStyle(ChatFormatting.GRAY);
                private static final Component CURRENT_OPTION_TEXT = Utils.translation("screen.ninjaphenix_container_lib.current_option_notice").withStyle(ChatFormatting.GOLD);

                @Override
                public void onTooltip(Button button, PoseStack stack, int x, int y) {
                    List<Component> tooltip = new ArrayList<>(4);
                    tooltip.add(button.getMessage());
                    if (isSelected) {
                        tooltip.add(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        tooltip.add(WARN_TEXT_1);
                        tooltip.add(WARN_TEXT_2);
                    }
                    PickScreen.this.renderTooltip(stack, tooltip, Optional.empty(), x, y);
                }

                @Override
                public void narrateTooltip(Consumer<Component> consumer) {
                    if (isSelected) {
                        consumer.accept(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        consumer.accept(WARN_TEXT_1.append(WARN_TEXT_2));
                    }
                }
            };
            optionWidgets.add(this.addRenderableWidget(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
                    settings.getLeft(), settings.getMiddle(), isWarn, option.equals(currentOption), button -> this.updatePlayerPreference(option), tooltip)));
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
