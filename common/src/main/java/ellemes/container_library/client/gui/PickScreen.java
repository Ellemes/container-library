package ellemes.container_library.client.gui;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.blaze3d.vertex.PoseStack;
import ellemes.container_library.CommonMain;
import ellemes.container_library.api.client.function.ScreenSizePredicate;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.client.PickButton;
import ellemes.container_library.client.gui.widget.ScreenPickButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
    private final Set<ResourceLocation> options = ImmutableSortedSet.copyOf(PickScreen.BUTTON_SETTINGS.keySet());
    private final Supplier<Screen> returnToScreen;
    private final List<ScreenPickButton> optionButtons = new ArrayList<>(options.size());
    private final @NotNull Runnable onOptionPicked;
    private final AbstractHandler handler;
    private int topPadding;

    public PickScreen(Supplier<Screen> returnToScreen, @Nullable AbstractHandler handler) {
        super(Component.translatable("screen.ellemes_container_lib.screen_picker_title"));
        this.returnToScreen = returnToScreen;
        this.handler = handler;
        this.onOptionPicked = () -> {
        };
    }

    public PickScreen(@NotNull Runnable onOptionPicked) {
        super(Component.translatable("screen.ellemes_container_lib.screen_picker_title"));
        this.returnToScreen = () -> null;
        this.handler = null;
        this.onOptionPicked = onOptionPicked;
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareButtonSettings(ResourceLocation type, ResourceLocation texture, Component title, ScreenSizePredicate warningTest, List<Component> warningText) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(type, new PickButton(texture, title, warningTest, warningText));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        if (handler != null) {
            ResourceLocation preference = CommonMain.getConfigWrapper().getPreferredScreenType();
            int invSize = handler.getInventory().getContainerSize();
            if (AbstractScreen.getScreenSize(preference, invSize, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight()) == null) {
                minecraft.player.displayClientMessage(Component.translatable("generic.ellemes_container_lib.label").withStyle(ChatFormatting.GOLD).append(Component.translatable("chat.ellemes_container_lib.cannot_display_screen", Component.translatable("screen." + preference.getNamespace() + "." + preference.getPath() + "_screen")).withStyle(ChatFormatting.WHITE)), false);
                minecraft.player.closeContainer();
                return;
            }
            handler.clearSlots();
        }
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
        ResourceLocation preference = CommonMain.getConfigWrapper().getPreferredScreenType();
        int choices = options.size();
        int columns = Math.min(Mth.intFloorDiv(width, 96), choices);
        int innerPadding = Math.min((width - columns * 96) / (columns + 1), 20); // 20 is smallest gap for any screen.
        int outerPadding = (width - (((columns - 1) * innerPadding) + (columns * 96))) / 2;
        int x = 0;
        int topPadding = (height - 96) / 2;
        this.topPadding = topPadding;
        optionButtons.clear();
        for (ResourceLocation option : options) {
            PickButton settings = PickScreen.BUTTON_SETTINGS.get(option);
            boolean isWarn = settings.getWarningTest().test(width, height);
            boolean isCurrent = option.equals(preference);
            Button.OnTooltip tooltip = new Button.OnTooltip() {
                private static final Component CURRENT_OPTION_TEXT = Component.translatable("screen.ellemes_container_lib.current_option_notice").withStyle(ChatFormatting.GOLD);

                @Override
                public void onTooltip(Button button, PoseStack stack, int x, int y) {
                    List<Component> tooltip = new ArrayList<>(4);
                    tooltip.add(button.getMessage());
                    if (isCurrent) {
                        tooltip.add(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        tooltip.addAll(settings.getWarningText());
                    }
                    PickScreen.this.renderTooltip(stack, tooltip, Optional.empty(), x, y);
                }

                @Override
                public void narrateTooltip(Consumer<Component> consumer) {
                    if (isCurrent) {
                        consumer.accept(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        MutableComponent text = Component.literal("");
                        for (Component component : settings.getWarningText()) {
                            text.append(component);
                        }
                        consumer.accept(text);
                    }
                }
            };
            optionButtons.add(this.addRenderableWidget(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
                    settings.getTexture(), settings.getTitle(), isWarn, isCurrent, button -> this.updatePlayerPreference(option), tooltip)));
            x++;
        }
    }

    private void updatePlayerPreference(ResourceLocation selection) {
        CommonMain.getConfigWrapper().setPreferredScreenType(selection);
        onOptionPicked.run();
        this.onClose();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        optionButtons.forEach(button -> button.renderButtonTooltip(stack, mouseX, mouseY));
        GuiComponent.drawCenteredString(stack, font, title, width / 2, Math.max(topPadding / 2, 0), 0xFFFFFFFF);
    }
}
