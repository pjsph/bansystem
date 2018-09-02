package me.pjsph.bansystem.storage.yml;

import me.pjsph.bansystem.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class InfosYML {

    private static File BASE_DIR = new File(Main.getInstance().getDataFolder(), "datas");

    private Main plugin;
    private final String name = "infos";
    private File file;
    private YamlConfiguration configuration;

    public InfosYML(Main plugin) {
        this.plugin = plugin;
        this.file = new File(BASE_DIR, name + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.getConfigurationSection("infos") == null) {
            this.configuration.createSection("infos");
            save();
        }

    }

    public void write(String playerName, UUID uuid) {
        final ConfigurationSection section = configuration.getConfigurationSection("infos");

        section.set(playerName, uuid.toString());

        this.save();
    }

    public boolean exist(String playerName) {
        final ConfigurationSection section = configuration.getConfigurationSection("infos");

        for(String name : section.getKeys(false)) {
            if(name.equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    public OfflinePlayer read(String playerName) {
        if(!exist(playerName)) throw new NullPointerException();

        final ConfigurationSection section = configuration.getConfigurationSection("infos");

        UUID uuid = null;
        for(String name : section.getKeys(false)) {
            if(name.equalsIgnoreCase(playerName)) {
                uuid = UUID.fromString(section.getString(name));
            }
        }

        if(uuid == null) {
            throw new NullPointerException("The player has no information on the hard disk");
        }

        return Bukkit.getOfflinePlayer(uuid);
    }

    public HashMap<String, UUID> read() {
        final ConfigurationSection section = configuration.getConfigurationSection("infos");

        HashMap<String, UUID> map = new HashMap<>();
        for(String playerName : section.getKeys(false)) {
            String uuidName = section.getString(playerName);
            UUID uuid = UUID.fromString(uuidName);

            map.put(playerName, uuid);
        }

        return map;
    }

    private void save() {
        try {
            configuration.save(file);
        } catch(IOException e) {
            plugin.getLogger().severe("Cannot save file on hard disk!");
        }
    }

    static {
        if(!BASE_DIR.exists())
            BASE_DIR.mkdirs();
    }

}
