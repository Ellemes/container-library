package ninjaphenix.container_library;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import ninjaphenix.container_library.client.ForgeKeyHandler;
import ninjaphenix.container_library.client.gui.PageScreen;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.wrappers.PlatformUtils;

@Mod(Utils.MOD_ID)
public final class Main {
    public Main() {
        PlatformUtils.initialize(FMLLoader.getDist() == Dist.CLIENT ? new ForgeKeyHandler() : null, ModList.get()::isLoaded);

        CommonMain.initialize((menuType, factory) -> new MenuType<>((IContainerFactory<?>) factory::create).setRegistryName(menuType));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(MenuType.class, (RegistryEvent.Register<MenuType<?>> event) -> {
            IForgeRegistry<MenuType<?>> registry = event.getRegistry();
            registry.registerAll(CommonMain.getMenuType());
        });
        modEventBus.addListener((FMLClientSetupEvent event) -> {
            MenuScreens.register(CommonMain.getMenuType(), AbstractScreen::createScreen);
        });
        if (PlatformUtils.isClient()) {
            this.registerConfigGuiHandler();
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, (GuiScreenEvent.InitGuiEvent.Post event) -> {
                if (event.getGui() instanceof PageScreen screen) {
                    screen.addPageButtons();
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT) // Required unless moved to client only class, tries to class load Screen.
    private void registerConfigGuiHandler() {
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> {
                    return new PickScreen(() -> screen, null);
                })
        );
    }
}
