package ellemes.container_library.quilt;

import ellemes.container_library.thread.ThreadMain;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class QuiltMain implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        ThreadMain.initialize(
                QuiltLoader::isModLoaded,
                QuiltNetworkWrapper::new
        );
    }
}
