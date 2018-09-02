package me.pjsph.bansystem.storage.yml;

import me.pjsph.bansystem.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanYML {

    private static File BASE_DIR = new File(Main.getInstance().getDataFolder(), "datas");

    private Main plugin;
    private final String name = "bans";
    private File file;
    private YamlConfiguration configuration;

    public BanYML(Main plugin) {
        this.plugin = plugin;
        this.file = new File(BASE_DIR, name + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.getConfigurationSection("banned") == null) {
            this.configuration.createSection("banned");
            save();
        }
    }

    public void write(UUID uuid, long end, String reason) {
        final ConfigurationSection section = configuration.getConfigurationSection("banned");

        section.set(uuid.toString() + ".end", end);
        section.set(uuid.toString() + ".reason", reason);

        save();
    }

    public void delete(UUID uuid) {
        final ConfigurationSection section = configuration.getConfigurationSection("banned");

        for(String suuid : section.getKeys(false)) {
            if(suuid.equalsIgnoreCase(uuid.toString())) {
                section.set(suuid, null);
            }
        }

        this.save();
    }

    public HashMap<UUID, HashMap<String, String>> read() {
        final ConfigurationSection section = configuration.getConfigurationSection("banned");

        HashMap<UUID, HashMap<String, String>> map = new HashMap<>();

        for(String suuid : section.getKeys(false)) {
            UUID uuid = UUID.fromString(suuid);

            String end = String.valueOf(section.getLong(suuid + ".end"));
            String reason = section.getString(suuid + ".reason");

            HashMap<String, String> datas = new HashMap<>();

            datas.put("end", end);
            datas.put("reason", reason);

            map.put(uuid, datas);
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
