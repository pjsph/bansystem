package me.pjsph.bansystem.cache;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.bans.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class BanCache {

    private Map<UUID, HashMap<Database, String>> banned;
    private List<UUID> unbanned = new ArrayList<>();

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void update() {
        //DATABASE
        if(Main.getInstance().configManager.USE_DATABASE) {
            console.sendMessage(Main.getInstance().prefix + "§aUsing database");

            //START
            if (banned == null) {
                banned = new HashMap<>();

                console.sendMessage(Main.getInstance().prefix + "§bCaching...");

                //BANS
                Main.getInstance().getMysql().query("SELECT * FROM " + BanManager.TABLE, rs -> {
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();

                        while(rs.next()) {
                            String uuidName = rs.getString("player_uuid");
                            UUID uuid = UUID.fromString(uuidName);
                            long end = rs.getLong("end");
                            String reason = rs.getString("reason");

                            HashMap<Database, String> map = new HashMap<>();
                            map.put(Database.PLAYER_UUID, uuidName);
                            map.put(Database.END, String.valueOf(end));
                            map.put(Database.REASON, reason);

                            banned.put(uuid, map);
                        }
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });

            //STOP
            } else {

                console.sendMessage(Main.getInstance().prefix + "§bSaving cache...");

                //BANS
                console.sendMessage(Main.getInstance().prefix + " §8- Saving banned...");
                for(UUID uuid : banned.keySet()) {
                    if(isDatabaseBanned(uuid)) {
                        Main.getInstance().getMysql().update("UPDATE " + BanManager.TABLE + " SET end='" + banned.get(uuid).get(Database.END) + "', reason='" + banned.get(uuid).get(Database.REASON) + "' WHERE player_uuid='" + uuid.toString() + "'");
                    } else {
                        Main.getInstance().getMysql().update("INSERT INTO " + BanManager.TABLE + " (player_uuid, end, reason) VALUES ('" + uuid.toString() + "', '" + banned.get(uuid).get(Database.END) + "', '" + banned.get(uuid).get(Database.REASON) + "')");
                    }
                }

                //UNBANNED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unbanned...");
                for(UUID uuid : unbanned) {
                    Main.getInstance().getMysql().update("DELETE FROM " + BanManager.TABLE + " WHERE player_uuid='" + uuid.toString() + "'");
                }
            }

        //HARD DISK
        } else {

            console.sendMessage(Main.getInstance().prefix + "§aUsing hard disk");

            //START
            if (banned == null) {
                banned = new HashMap<>();

                console.sendMessage(Main.getInstance().prefix + "§bCaching...");

                //BANS
                HashMap<UUID, HashMap<String, String>> map = Main.getInstance().banYML.read();

                for(UUID uuid : map.keySet()) {
                    String end = map.get(uuid).get("end");
                    String reason = map.get(uuid).get("reason");

                    HashMap<Database, String> values = new HashMap<>();
                    values.put(Database.PLAYER_UUID, uuid.toString());
                    values.put(Database.END, end);
                    values.put(Database.REASON, reason);

                    banned.put(uuid, values);
                }
            //STOP
            } else {

                console.sendMessage(Main.getInstance().prefix + "§bSaving cache...");

                //BANS
                console.sendMessage(Main.getInstance().prefix + " §8- Saving banned...");
                for(UUID uuid : banned.keySet()) {
                    Main.getInstance().banYML.write(uuid, Long.parseLong(banned.get(uuid).get(Database.END)), banned.get(uuid).get(Database.REASON));
                }

                //UNBANNED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unbanned...");
                for(UUID uuid : unbanned) {
                    Main.getInstance().banYML.delete(uuid);
                }
            }
        }
    }

    //BANS
    public void ban(UUID uuid, long end, String reason) {
        HashMap<Database, String> values = banned.get(uuid);
        if(values == null) {
            HashMap<Database, String> map = new HashMap<>();
            map.put(Database.PLAYER_UUID, uuid.toString());
            map.put(Database.END, String.valueOf(end));
            map.put(Database.REASON, reason);
            banned.put(uuid, map);
        }

        if(unbanned.contains(uuid)) unbanned.remove(uuid);
    }

    public void unban(UUID uuid) {
        banned.remove(uuid);
        unbanned.add(uuid);
    }

    public boolean isBanned(UUID uuid) {
        return banned.containsKey(uuid);
    }

    public boolean isDatabaseBanned(UUID uuid) {
        return (boolean) Main.getInstance().getMysql().query("SELECT * FROM " + BanManager.TABLE + " WHERE player_uuid='" + uuid.toString() + "'", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public long getBanEnd(UUID uuid) {
        return Long.parseLong(banned.get(uuid).get(Database.END));
    }

    public String getBanReason(UUID uuid) {
        return banned.get(uuid).get(Database.REASON);
    }
}

enum Database {

    PLAYER_UUID,
    END,
    REASON

}
