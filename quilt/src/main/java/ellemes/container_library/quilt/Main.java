package ellemes.container_library.quilt;

import ellemes.container_library.thread.Thread;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Thread.initialize(MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT, QuiltLoader::isModLoaded, QuiltLoader.getConfigDir());
    }
}
