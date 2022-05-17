package ellemes.container_library.forge.wrappers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import ellemes.container_library.config.ConfigV0;
import ellemes.container_library.forge.config.LegacyFactory;
import ellemes.container_library.wrappers.ConfigWrapper;

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
