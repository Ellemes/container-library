package ellemes.container_library.quilt.wrappers;

import ellemes.container_library.config.ConfigV0;
import ellemes.container_library.quilt.config.LegacyFactory;
import ellemes.container_library.wrappers.ConfigWrapper;

import java.nio.file.Path;

public final class ConfigWrapperImpl extends ConfigWrapper {
    public ConfigWrapperImpl(Path configPath, Path oldConfigPath) {
        super(configPath, oldConfigPath);
    }

    @Override
    protected ConfigV0 readOldConfig(String configLines, Path oldConfigPath) {
        return this.convertToConfig(configLines, LegacyFactory.INSTANCE, oldConfigPath);
    }
}
