package ellemes.container_library;

import ellemes.container_library.api.v2.OpenableBlockEntityProviderV2;
import ellemes.container_library.api.v3.OpenableInventoryProvider;
import ellemes.container_library.api.v3.client.ScreenOpeningApi;
import ellemes.container_library.api.v3.client.ScreenTypeApi;
import ellemes.container_library.client.KeyHandler;
import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.ScrollScreen;
import ellemes.container_library.client.gui.SingleScreen;
import ellemes.container_library.wrappers.ConfigWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommonClient {
    private static ConfigWrapper configWrapper;
    private static KeyHandler keyHandler;
    private static Function<String, Boolean> modLoadedFunction;

    public static void initialize(BiFunction<Path, Path, ConfigWrapper> configWrapperMaker, Path configPath, Path oldConfigPath,
                                  KeyHandler keyHandler, Function<String, Boolean> modLoadedFunction) {
        CommonClient.configWrapper = configWrapperMaker.apply(configPath, oldConfigPath);
        CommonClient.keyHandler = keyHandler;
        CommonClient.modLoadedFunction = modLoadedFunction;
        ScreenTypeApi.registerScreenButton(Utils.PAGE_SCREEN_TYPE,
                Utils.id("textures/gui/page_button.png"),
                Component.translatable("screen.ellemes_container_lib.page_screen"));
        ScreenTypeApi.registerScreenButton(Utils.SCROLL_SCREEN_TYPE,
                Utils.id("textures/gui/scroll_button.png"),
                Component.translatable("screen.ellemes_container_lib.scroll_screen"));
        ScreenTypeApi.registerScreenButton(Utils.SINGLE_SCREEN_TYPE,
                Utils.id("textures/gui/single_button.png"),
                Component.translatable("screen.ellemes_container_lib.single_screen"),
                (scaledWidth, scaledHeight) -> scaledWidth < 370 || scaledHeight < 386, // Smallest possible resolution a double netherite chest fits on.
                List.of(
                        Component.translatable("screen.ellemes_container_lib.off_screen_warning_1").withStyle(ChatFormatting.GRAY),
                        Component.translatable("screen.ellemes_container_lib.off_screen_warning_2").withStyle(ChatFormatting.GRAY)
                ));

        ScreenTypeApi.registerScreenType(Utils.PAGE_SCREEN_TYPE, PageScreen::new);
        ScreenTypeApi.registerScreenType(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::new);
        ScreenTypeApi.registerScreenType(Utils.SINGLE_SCREEN_TYPE, SingleScreen::new);

        // todo: these settings leave no room for rei/jei should we take those into consideration for minimum screen width
        ScreenTypeApi.registerDefaultScreenSize(Utils.PAGE_SCREEN_TYPE, PageScreen::retrieveScreenSize);
        ScreenTypeApi.registerDefaultScreenSize(Utils.SCROLL_SCREEN_TYPE, ScrollScreen::retrieveScreenSize);
        ScreenTypeApi.registerDefaultScreenSize(Utils.SINGLE_SCREEN_TYPE, SingleScreen::retrieveScreenSize);

        ScreenTypeApi.setPrefersSingleScreen(Utils.PAGE_SCREEN_TYPE);
        ScreenTypeApi.setPrefersSingleScreen(Utils.SCROLL_SCREEN_TYPE);
    }

    public static ConfigWrapper getConfigWrapper() {
        return configWrapper;
    }

    public static boolean isConfigKeyPressed(int keyCode, int scanCode, int modifiers) {
        return CommonClient.keyHandler.isKeyPressed(keyCode, scanCode, modifiers);
    }

    public static boolean isModLoaded(String modId) {
        return CommonClient.modLoadedFunction.apply(modId);
    }

    public static boolean tryOpenSpectatorInventory(ClientLevel world, Player player, HitResult hit, InteractionHand hand) {
        if (player.isSpectator()) {
            switch (hit.getType()) {
                case BLOCK -> {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockState state = world.getBlockState(blockHit.getBlockPos());
                    Block block = state.getBlock();
                    if (block instanceof OpenableBlockEntityProviderV2) {
                        if (state.use(world, player, hand, blockHit) == InteractionResult.SUCCESS) {
                            Minecraft.getInstance().gameMode.startPrediction(world, i -> {
                                return new ServerboundUseItemOnPacket(hand, blockHit, i);
                            });
                        }
                        return true;
                    } else if (block instanceof OpenableInventoryProvider<?>) {
                        ScreenOpeningApi.openBlockInventory(blockHit.getBlockPos());
                        return true;
                    }
                }
                case ENTITY -> {
                    Entity entity = ((EntityHitResult) hit).getEntity();
                    if (entity instanceof OpenableInventoryProvider<?>) {
                        ScreenOpeningApi.openEntityInventory(entity);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
