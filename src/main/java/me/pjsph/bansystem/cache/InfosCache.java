package me.pjsph.bansystem.cache;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.infos.PlayerInfos;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfosCache {

    private Map<String, UUID> player_infos;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void update() {

        //DATABASE
        if (Main.getInstance().configManager.USE_DATABASE) {
            //START
            if (player_infos == null) {
                player_infos = new HashMap<>();

                //PLAYER_INFOS
                Main.getInstance().getMysql().query("SELECT * FROM " + PlayerInfos.TABLE, rs -> {
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();

                        while (rs.next()) {
                            String playerName = rs.getString("player_name");
                            String uuidName = rs.getString("player_uuid");
                            UUID uuid = UUID.fromString(uuidName);

                            player_infos.put(playerName, uuid);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                console.sendMessage(Main.getInstance().prefix + "§aCached!");

            //STOP
            } else {

                //PLAYER_INFOS
                console.sendMessage(Main.getInstance().prefix + " §8- Updating player infos...");
                for (String playerName : player_infos.keySet()) {
                    if (!isDatabaseInfos(playerName)) {
                        Main.getInstance().getMysql().update("INSERT INTO " + PlayerInfos.TABLE + " (player_name, player_uuid) VALUES ('" + playerName + "', '" + player_infos.get(playerName) + "')");
                    }
                }
            }

            console.sendMessage(Main.getInstance().prefix + "§aCache saved!");

        //HARD DISK
        } else {
            //START
            if (player_infos == null) {

                //PLAYER_INFOS
                player_infos = Main.getInstance().infosYML.read();

                console.sendMessage(Main.getInstance().prefix + "§aCached!");

            //STOP
            } else {

                //PLAYER_INFOS
                console.sendMessage(Main.getInstance().prefix + " §8- Updating player infos...");
                for (String playerName : player_infos.keySet()) {
                    if (!Main.getInstance().infosYML.exist(playerName)) {
                        Main.getInstance().infosYML.write(playerName, player_infos.get(playerName));
                    }
                }

                console.sendMessage(Main.getInstance().prefix + "§aCache saved!");
            }
        }
    }

    //PLAYER_INFOS
    public void addInfo(String playerName, UUID uuid) {
        if (!player_infos.containsValue(uuid))
            player_infos.put(playerName, uuid);
    }

    public boolean existInfo(String playerName) {
        for (String name : player_infos.keySet()) {
            if (name.equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    public UUID getUuid(String playerName) {
        for (String name : player_infos.keySet()) {
            if (name.equalsIgnoreCase(playerName)) {
                return player_infos.get(name);
            }
        }

        throw new NullPointerException("Unable to find this uuid!");
    }

    public boolean isDatabaseInfos(String playerName) {
        return (boolean) Main.getInstance().getMysql().query("SELECT * FROM " + PlayerInfos.TABLE + " WHERE player_name='" + playerName + "'", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public Map<String, UUID> getPlayer_infos() {
        return player_infos;
    }
}
