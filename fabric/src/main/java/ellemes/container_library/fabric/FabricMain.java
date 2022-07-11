package ellemes.container_library.fabric;

import ellemes.container_library.thread.ThreadMain;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricMain implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();
        ThreadMain.initialize(
                loader::isModLoaded,
                FabricNetworkWrapper::new
        );
    }
}
