package ninjaphenix.container_library.wrappers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import ninjaphenix.container_library.config.ConfigV0;
import ninjaphenix.container_library.config.LegacyFactory;

import java.io.StringReader;
import java.nio.file.Path;

public final class ConfigWrapperImpl extends ConfigWrapper {
    public ConfigWrapperImpl(Path configPath, Path oldConfigPath) {
        super(configPath, oldConfigPath);
    }

    @Override
    protected ConfigV0 readOldConfig(String configLines, Path oldConfigPath) {
        CommentedConfig tomlConfig = TomlFormat.instance().createParser().parse(new StringReader(configLines));
        return this.convert(tomlConfig, -1, LegacyFactory.INSTANCE);
    }
}
