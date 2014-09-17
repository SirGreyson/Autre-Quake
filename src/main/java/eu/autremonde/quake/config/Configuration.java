/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.config;

import eu.autremonde.quake.AutreQuake;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Configuration {

    private AutreQuake plugin;
    private static Map<String, YamlConfiguration> loadedConfigs = new TreeMap<String, YamlConfiguration>(String.CASE_INSENSITIVE_ORDER);

    public Configuration(AutreQuake plugin) {
        this.plugin = plugin;
    }

    public void loadConfigurations() {
        loadConfig("config");
        loadConfig("lang");
        loadConfig("arenas");
        loadConfig("lobbies");
        loadConfig("railguns");
        loadConfig("stats");
    }

    public void saveConfigurations() {
        saveConfig("arenas");
        saveConfig("lobbies");
        saveConfig("stats");
    }

    private boolean isConfigLoaded(String fileName) {
        return loadedConfigs.containsKey(fileName);
    }

    public static YamlConfiguration getConfig(String fileName) {
        return loadedConfigs.get(fileName);
    }

    private void loadConfig(String fileName) {
        File configF = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!configF.exists()) {
            if (plugin.getResource(fileName + ".yml") != null) plugin.saveResource(fileName + ".yml", false);
            else {
                try {
                    configF.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Error! Could not create custom configuration: " + fileName);
                    e.printStackTrace();
                }
            }
        }
        if (!isConfigLoaded(fileName)) loadedConfigs.put(fileName, YamlConfiguration.loadConfiguration(configF));
    }

    private void reloadConfig(String fileName) {
        File configF = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!isConfigLoaded(fileName))
            plugin.getLogger().severe("Error! Tried to reload non-existent config file: " + fileName);
        else try {
            loadedConfigs.get(fileName).load(configF);
        } catch (IOException e) {
            plugin.getLogger().severe("Error! Could not reload custom configuration: " + fileName);
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe("Error! Could not reload custom configuration: " + fileName);
            e.printStackTrace();
        }
    }

    private void saveConfig(String fileName) {
        File configF = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!isConfigLoaded(fileName))
            plugin.getLogger().severe("Error! Tried to save non-existent config file: " + fileName);
        else try {
            loadedConfigs.get(fileName).save(configF);
        } catch (IOException e) {
            plugin.getLogger().severe("Error! Could not save custom configuration: " + fileName);
            e.printStackTrace();
        }
    }
}