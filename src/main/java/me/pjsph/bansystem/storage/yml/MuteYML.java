package me.pjsph.bansystem.storage.yml;

import me.pjsph.bansystem.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class MuteYML {

    private static File BASE_DIR = new File(Main.getInstance().getDataFolder(), "datas");

    private Main plugin;
    private final String name = "mutes";
    private File file;
    private YamlConfiguration configuration;

    public MuteYML(Main plugin) {
        this.plugin = plugin;
        this.file = new File(BASE_DIR, name + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.getConfigurationSection("muted") == null) {
            this.configuration.createSection("muted");
            save();
        }
    }

    public void write(UUID uuid, long end) {
        final ConfigurationSection section = configuration.getConfigurationSection("muted");

        section.set(uuid.toString(), end);

        save();
    }

    public void delete(UUID uuid) {
        final ConfigurationSection section = configuration.getConfigurationSection("muted");

        for(String suuid : section.getKeys(false)) {
            if(suuid.equalsIgnoreCase(uuid.toString())) {
                section.set(suuid, null);
            }
        }

        this.save();
    }

    public HashMap<UUID, Long> read() {
        final ConfigurationSection section = configuration.getConfigurationSection("muted");

        HashMap<UUID, Long> map = new HashMap<>();

        for(String suuid : section.getKeys(false)) {
            UUID uuid = UUID.fromString(suuid);

            long end = section.getLong(suuid);

            map.put(uuid, end);
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
