package me.pjsph.bansystem.storage.yml;

import me.pjsph.bansystem.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DefaultConfigManager {

    private Main plugin;
    private File saveFile;
    private YamlConfiguration config;

    public boolean USE_DATABASE;
    public String DB_URL;
    public String DB_NAME;
    public String USERNAME;
    public String PASSWORD;
    public String PORT;

    public DefaultConfigManager(Main main) {
        this.plugin = main;
        this.saveFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(saveFile);
    }

    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(saveFile);

        final ConfigurationSection section = this.getConfigurationSection("database");

        if(section != null) {

            if(section.getString("url") == null || section.getString("db_name") == null || section.getInt("port") == 0) {
                section.set("use_db", false);
                section.set("url", "localhost");
                section.set("port", 3306);
                section.set("db_name", "bansystem");
                section.set("username", "root");
                section.set("password", "");

                this.save();
            }

            USE_DATABASE = section.getBoolean("use_db");
            DB_URL = section.getString("url");
            PORT = String.valueOf(section.getInt("port"));
            DB_NAME = section.getString("db_name");
            USERNAME = section.getString("username");
            PASSWORD = section.getString("password");
        }
    }

    private ConfigurationSection getConfigurationSection(String key) {
        ConfigurationSection section = this.config.getConfigurationSection(key);

        if(section == null) {
            section = config.createSection(key);
        }

        return section;
    }

    private void save() {
        try {
            this.config.save(saveFile);
        } catch(IOException e) {
            plugin.getLogger().warning("Unable to save config file!");
        }
    }

}
