package ninjaphenix.container_library.wrappers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import ninjaphenix.container_library.config.ConfigV0;
import ninjaphenix.container_library.config.LegacyFactory;

import java.io.StringReader;
import java.nio.file.Path;

final class ConfigWrapperImpl extends ConfigWrapper {
    static ConfigWrapper getInternalInstance() {
        return new ConfigWrapperImpl();
    }

    @Override
    protected ConfigV0 readOldConfig(String configLines, Path oldConfigPath) {
        CommentedConfig tomlConfig = TomlFormat.instance().createParser().parse(new StringReader(configLines));
        return this.convert(tomlConfig, -1, LegacyFactory.INSTANCE);
    }
}
