package ellemes.container_library.fabric;

import ellemes.container_library.thread.ThreadClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricLoader loader = FabricLoader.getInstance();
        ThreadClient.initialize(loader.getConfigDir(), loader::isModLoaded);
    }
}
