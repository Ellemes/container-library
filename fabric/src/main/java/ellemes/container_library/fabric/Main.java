package ellemes.container_library.fabric;

import ellemes.container_library.thread.Thread;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();
        Thread.initialize(loader.getEnvironmentType() == EnvType.CLIENT, loader::isModLoaded, loader.getConfigDir());
    }
}
