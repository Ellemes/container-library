package ninjaphenix.container_library.wrappers;

import ninjaphenix.container_library.config.ConfigV0;
import ninjaphenix.container_library.config.LegacyFactory;

import java.nio.file.Path;

final class ConfigWrapperImpl extends ConfigWrapper {
    @Override
    protected ConfigV0 readOldConfig(String configLines, Path oldConfigPath) {
        return this.convertToConfig(configLines, LegacyFactory.INSTANCE, oldConfigPath);
    }
}
