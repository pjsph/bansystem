package me.pjsph.bansystem;

import me.pjsph.bansystem.bans.BanManager;
import me.pjsph.bansystem.cache.Cache;
import me.pjsph.bansystem.commands.Commands;
import me.pjsph.bansystem.database.MySQL;
import me.pjsph.bansystem.infos.PlayerInfos;
import me.pjsph.bansystem.listeners.PlayerChat;
import me.pjsph.bansystem.listeners.PlayerJoin;
import me.pjsph.bansystem.mutes.MuteManager;
import me.pjsph.bansystem.storage.yml.BanYML;
import me.pjsph.bansystem.storage.yml.DefaultConfigManager;
import me.pjsph.bansystem.storage.yml.InfosYML;
import me.pjsph.bansystem.storage.yml.MuteYML;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private BasicDataSource connectionPool;
    private MySQL mysql;
    public BanManager banManager = new BanManager();
    public MuteManager muteManager = new MuteManager();
    public PlayerInfos playerInfos = new PlayerInfos();
    public Cache cache = new Cache();
    public DefaultConfigManager configManager;
    public BanYML banYML;
    public InfosYML infosYML;
    public MuteYML muteYML;

    public final String prefix = "§c[BanSystem]§r ";

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.configManager = new DefaultConfigManager(this);
        this.configManager.loadConfig();

        banYML = new BanYML(this);
        infosYML = new InfosYML(this);
        muteYML = new MuteYML(this);

        if(configManager.USE_DATABASE)
            initConnection();

        registerListeners();
        registerCommands();

        cache.update();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.configManager.loadConfig();

        cache.update();

        super.onDisable();
    }

    private void registerCommands() {
        getCommand("ban").setExecutor(new Commands());
        getCommand("unban").setExecutor(new Commands());
        getCommand("mute").setExecutor(new Commands());
        getCommand("unmute").setExecutor(new Commands());
        getCommand("bansystem").setExecutor(new Commands());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerChat(), this);
    }

    public void initConnection() {
        if(connectionPool != null) {
            try {
                connectionPool.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        connectionPool = new BasicDataSource();
        connectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        connectionPool.setUsername(configManager.USERNAME);
        connectionPool.setPassword(configManager.PASSWORD);
        connectionPool.setUrl("jdbc:mysql://" + configManager.DB_URL + ":" + configManager.PORT + "/" + configManager.DB_NAME + "?autoReconnect=true");
        connectionPool.setInitialSize(1);
        connectionPool.setMaxTotal(10);
        mysql = new MySQL(connectionPool);
        mysql.createTables();
    }

    public MySQL getMysql() {
        return mysql;
    }

    public static Main getInstance() {
        return INSTANCE;
    }
}
