package ellemes.container_library.quilt;

import ellemes.container_library.thread.ThreadClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class QuiltClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        ThreadClient.initialize(QuiltLoader.getConfigDir(), QuiltLoader::isModLoaded);
    }
}
