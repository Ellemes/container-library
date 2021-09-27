package ninjaphenix.container_library.client.gui;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.function.ScreenSizePredicate;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.client.PickButton;
import ninjaphenix.container_library.client.gui.widget.ScreenPickButton;
import ninjaphenix.container_library.wrappers.ConfigWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class PickScreen extends Screen {
    private static final Map<ResourceLocation, PickButton> BUTTON_SETTINGS = new HashMap<>();
    private final Set<ResourceLocation> options = ImmutableSortedSet.copyOf(PickScreen.BUTTON_SETTINGS.keySet());
    private final Supplier<Screen> returnToScreen;
    private final List<ScreenPickButton> optionButtons = new ArrayList<>(options.size());
    private final @NotNull Runnable onOptionPicked;
    private final AbstractHandler handler;
    private int topPadding;

    public PickScreen(Supplier<Screen> returnToScreen, AbstractHandler handler) {
        super(new TranslationTextComponent("screen.ninjaphenix_container_lib.screen_picker_title"));
        this.returnToScreen = returnToScreen;
        this.handler = handler;
        this.onOptionPicked = () -> {};
    }

    public PickScreen(@NotNull Runnable onOptionPicked) {
        super(new TranslationTextComponent("screen.ninjaphenix_container_lib.screen_picker_title"));
        this.returnToScreen = () -> null;
        this.handler = null;
        this.onOptionPicked = onOptionPicked;
    }

    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void declareButtonSettings(ResourceLocation type, ResourceLocation texture, ITextComponent title, ScreenSizePredicate warningTest, List<ITextComponent> warningText) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(type, new PickButton(texture, title, warningTest, warningText));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        if (handler != null) {
            ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
            int invSize = handler.getInventory().getContainerSize();
            if (AbstractScreen.getScreenSize(preference, invSize, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight()) == null) {
                minecraft.player.displayClientMessage(Utils.translation("generic.ninjaphenix_container_lib.label").withStyle(TextFormatting.GOLD).append(Utils.translation("chat.ninjaphenix_container_lib.cannot_display_screen", Utils.translation("screen." + preference.getNamespace() + "." + preference.getPath() + "_screen")).withStyle(TextFormatting.WHITE)), false);
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
        ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
        int choices = options.size();
        int columns = Math.min(MathHelper.intFloorDiv(width, 96), choices);
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
            Button.ITooltip tooltip = new Button.ITooltip() {
                private final ITextComponent CURRENT_OPTION_TEXT = Utils.translation("screen.ninjaphenix_container_lib.current_option_notice").withStyle(TextFormatting.GOLD);

                @Override
                public void onTooltip(Button button, MatrixStack stack, int x, int y) {
                    List<ITextComponent> tooltip = new ArrayList<>(4);
                    tooltip.add(button.getMessage());
                    if (isCurrent) {
                        tooltip.add(CURRENT_OPTION_TEXT);
                    }
                    if (isWarn) {
                        tooltip.addAll(settings.getWarningText());
                    }
                    PickScreen.this.renderComponentTooltip(stack, tooltip, x, y); // Not sure about this.
                }
            };
            optionButtons.add(this.addButton(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
                    settings.getTexture(), settings.getTitle(), isWarn, isCurrent, button -> this.updatePlayerPreference(option), tooltip)));
            x++;
        }
    }

    private void updatePlayerPreference(ResourceLocation selection) {
        ConfigWrapper.getInstance().setPreferredScreenType(selection);
        onOptionPicked.run();
        this.onClose();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        optionButtons.forEach(button -> button.renderButtonTooltip(stack, mouseX, mouseY));
        AbstractGui.drawCenteredString(stack, font, title, width / 2, Math.max(topPadding / 2, 0), 0xFFFFFFFF);
    }
}
