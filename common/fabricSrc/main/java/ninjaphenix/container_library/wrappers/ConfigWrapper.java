package ninjaphenix.container_library.wrappers;

import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.CommonMain;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.config.Config;
import ninjaphenix.container_library.config.ConfigV0;
import ninjaphenix.container_library.config.Converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ConfigWrapper {
    private static ConfigWrapper INSTANCE;
    private ConfigV0 config;
    // todo: remove old config code in 1.18, rather repurpose it for expandedstorage.json -> ninjaphenix-container-library.json
    private Path configPath, oldConfigPath;

    public static ConfigWrapper getInstance() {
        if (ConfigWrapper.INSTANCE == null) {
            ConfigWrapper.INSTANCE = new ConfigWrapperImpl();
        }
        return ConfigWrapper.INSTANCE;
    }

    public final void initialise(Path configPath, Path oldConfigPath) {
        this.configPath = configPath;
        this.oldConfigPath = oldConfigPath;
        config = this.getConfig();
    }

    public final boolean isScrollingUnrestricted() {
        return !config.isScrollingRestricted();
    }

    public final boolean preferSmallerScreens() {
        return config.preferSmallerScreens();
    }

    public final Identifier getPreferredScreenType() {
        return config.getScreenType();
    }

    public final void setPreferredScreenType(Identifier type) {
        //noinspection deprecation
        if (AbstractScreen.isScreenTypeDeclared(type) && type != config.getScreenType()) {
            config.setScreenType(type);
            this.saveConfig(config);
        }
    }

    /**
     * Should always return the raw old config text. If existing config is given return that else the parsed old config.
     */
    protected abstract ConfigV0 readOldConfig(String configLines, Path oldConfigPath);

    // protected final
    private ConfigV0 getConfig() {
        boolean triedLoadingOldConfig = false;
        boolean triedLoadingNewConfig = false;
        ConfigV0 config = null;
        if (Files.exists(configPath)) {
            triedLoadingNewConfig = true;
            config = this.loadConfig(configPath, ConfigV0.Factory.INSTANCE, false);
        }
        if (Files.exists(oldConfigPath)) {
            triedLoadingOldConfig = true;
            try (BufferedReader reader = Files.newBufferedReader(oldConfigPath)) {
                String configLines = reader.lines().collect(Collectors.joining());
                if (config == null) {
                    ConfigV0 oldConfig = this.readOldConfig(configLines, oldConfigPath);
                    if (oldConfig != null) {
                        config = oldConfig;
                        this.saveConfig(config);
                    }
                }
                this.backupFile(oldConfigPath, String.format("Failed to backup legacy Expanded Storage config, '%s'.", oldConfigPath.getFileName().toString()), configLines);
            } catch (IOException e) {
                if (config == null) {
                    CommonMain.LOGGER.warn("Failed to load legacy Expanded Storage Config, new default config will be used.", e);
                }
            }
        }
        if (config == null) {
            if (triedLoadingOldConfig || triedLoadingNewConfig) {
                CommonMain.LOGGER.warn("Could not load an existing config, Expanded Storage is using it's default config.");
            }
            config = new ConfigV0();
            this.saveConfig(config);
        }
        return config;
    }

    protected final <T extends Config> void saveConfig(T config) {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            Map<String, Object> configValues = config.getConverter().toSource(config);
            configValues.put("config_version", config.getVersion());
            Utils.GSON.toJson(configValues, Utils.MAP_TYPE, Utils.GSON.newJsonWriter(writer));
        } catch (IOException e) {
            CommonMain.LOGGER.warn("Failed to save Expanded Storage's config.", e);
        }
    }

    // Tries to load a config file, returns null if loading fails.
    // Will need to be reworked to allow converting between ConfigV0 and ConfigV1
    // essentially converter will need to be decided in this method based on the value of "config_version"
    protected final <T extends Config> T convertToConfig(String lines, Converter<Map<String, Object>, T> converter, Path configPath) {
        try {
            Map<String, Object> configMap = Utils.GSON.fromJson(lines, Utils.MAP_TYPE);
            // Do not edit, gson returns a double, we want an int.
            int configVersion = MathHelper.floor((Double) configMap.getOrDefault("config_version", -1.0D));
            return this.convert(configMap, configVersion, converter);
        } catch (JsonParseException e) {
            String configFileName = configPath.getFileName().toString();
            CommonMain.warnThrowableMessage("Failed to convert config, backing up config '{}'.", e, configFileName);
            this.backupFile(configPath, String.format("Failed to backup expanded storage config which failed to read, '%s'.%n", configFileName), lines);
            return null;
        }
    }

    protected final <A, B extends Config> B convert(A config, int configVersion, Converter<A, B> converter) {
        if (configVersion == converter.getSourceVersion()) {
            B returnValue = converter.fromSource(config);
            if (returnValue == null) {
                return null;
            }
            if (returnValue.getVersion() == converter.getTargetVersion()) {
                return returnValue;
            } else {
                throw new IllegalStateException(String.format("CODE ERROR: Converter converted to an invalid config, expected version %s, got %s.", converter.getTargetVersion(), returnValue.getVersion()));
            }
        } else {
            throw new IllegalStateException(String.format("CODE ERROR: Converter converted to an invalid config, expected version %s, got %s.", converter.getSourceVersion(), configVersion));
        }
    }

    protected final void backupFile(Path path, String failureMessage, String contents) {
        try {
            Path backupPath = path.resolveSibling(path.getFileName() + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date()) + ".backup");
            Files.move(path, backupPath);
        } catch (IOException e) {
            CommonMain.LOGGER.warn(failureMessage, e);
            if (contents != null) {
                CommonMain.LOGGER.warn(contents);
            }
        }
    }

    protected final <T extends Config> T loadConfig(Path configPath, Converter<Map<String, Object>, T> converter, boolean isLegacy) {
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String configLines = reader.lines().collect(Collectors.joining());
            return this.convertToConfig(configLines, converter, configPath);
        } catch (IOException e) {
            String configFileName = configPath.getFileName().toString();
            CommonMain.warnThrowableMessage("Failed to read {}Expanded Storage config, '{}'.", e, isLegacy ? "legacy " : "", configFileName);
            e.printStackTrace();
            this.backupFile(configPath, String.format("Failed to backup %sExpanded Storage config, '%s'.%n", isLegacy ? "legacy " : "", configFileName), null);
        }
        return null;
    }
}
