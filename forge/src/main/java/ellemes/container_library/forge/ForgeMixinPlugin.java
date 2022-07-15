package ellemes.container_library.forge;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ForgeMixinPlugin implements IMixinConfigPlugin {
    private static final int MIXIN_PACKAGE_LENGTH = "ellemes.container_library.forge.mixin".length() + 1;

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean isQuarkPresent = FMLLoader.getLoadingModList().getModFileById("quark") != null;
        String className = mixinClassName.substring(MIXIN_PACKAGE_LENGTH);
        return switch (className) {
            case "QuarkButtonAllowedMixin", "QuarkRepositionButtonsMixin" -> isQuarkPresent;
            default -> true;
        };
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
