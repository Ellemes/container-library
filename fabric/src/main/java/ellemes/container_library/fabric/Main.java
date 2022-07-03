package ellemes.container_library.fabric;

import ellemes.container_library.thread.Thread;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        Thread.initialize(fabricLoader.getEnvironmentType() == EnvType.CLIENT, fabricLoader::isModLoaded, fabricLoader.getConfigDir());
    }
}
