package ellemes.container_library.forge;

import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.PickScreen;
import ellemes.container_library.forge.client.ForgeKeyHandler;
import ellemes.container_library.forge.wrappers.ConfigWrapperImpl;
import ellemes.container_library.forge.wrappers.NetworkWrapperImpl;
import ellemes.container_library.wrappers.PlatformUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegisterEvent;

@Mod("ellemes_container_lib")
public final class Main {
    public Main() {
        PlatformUtils.initialize(FMLLoader.getDist() == Dist.CLIENT ? new ForgeKeyHandler() : null, ModList.get()::isLoaded);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CommonMain.initialize((handlerType, factory) -> {
            MenuType<?> menuType = new MenuType<>((IContainerFactory<?>) factory::create);

            modEventBus.addListener((RegisterEvent event) -> {
                event.register(Registry.MENU_REGISTRY, helper -> {
                    helper.register(handlerType, menuType);
                });
            });

            return menuType;
            },
                FMLPaths.CONFIGDIR.get().resolve(Utils.CONFIG_PATH),
                FMLPaths.CONFIGDIR.get().resolve(Utils.FORGE_LEGACY_CONFIG_PATH),
                ConfigWrapperImpl::new, new NetworkWrapperImpl());
        modEventBus.addListener((FMLClientSetupEvent event) -> MenuScreens.register(CommonMain.getScreenHandlerType(), AbstractScreen::createScreen));
        if (PlatformUtils.isClient()) {
            this.registerConfigGuiHandler();
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, (ScreenEvent.InitScreenEvent.Post event) -> {
                if (event.getScreen() instanceof PageScreen screen) {
                    screen.addPageButtons();
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT) // Required unless moved to client only class, tries to class load Screen.
    private void registerConfigGuiHandler() {
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((client, screen) -> new PickScreen(() -> screen, null))
        );
    }
}
