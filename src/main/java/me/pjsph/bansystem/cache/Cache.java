package me.pjsph.bansystem.cache;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.bans.BanManager;
import me.pjsph.bansystem.infos.PlayerInfos;
import me.pjsph.bansystem.mutes.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class Cache {

    private Map<UUID, HashMap<Database, String>> banned;
    private List<UUID> unbanned = new ArrayList<>();
    private Map<UUID, Long> muted;
    private List<UUID> unmuted = new ArrayList<>();
    private Map<String, UUID> player_infos;
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void update() {

        //DATABASE
        if(Main.getInstance().configManager.USE_DATABASE) {
            console.sendMessage(Main.getInstance().prefix + "§aUsing database");

            //START
            if(banned == null) {
                banned = new HashMap<>();
                player_infos = new HashMap<>();
                muted = new HashMap<>();
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
                        console.sendMessage(Main.getInstance().prefix + "§aCached!");
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });

                //PLAYER_INFOS
                Main.getInstance().getMysql().query("SELECT * FROM " + PlayerInfos.TABLE, rs -> {
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();

                        while(rs.next()) {
                            String playerName = rs.getString("player_name");
                            String uuidName = rs.getString("player_uuid");
                            UUID uuid = UUID.fromString(uuidName);

                            player_infos.put(playerName, uuid);
                        }
                        console.sendMessage(Main.getInstance().prefix + "§aCached!");
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });

                //MUTES
                Main.getInstance().getMysql().query("SELECT * FROM " + MuteManager.TABLE, rs -> {
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();

                        while(rs.next()) {
                            String uuidName = rs.getString("player_uuid");
                            UUID uuid = UUID.fromString(uuidName);
                            long end = rs.getLong("end");

                            muted.put(uuid, Long.valueOf(end));
                        }
                        console.sendMessage(Main.getInstance().prefix + "§aCached!");
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

                //PLAYER_INFOS
                console.sendMessage(Main.getInstance().prefix + " §8- Updating player infos...");
                for(String playerName : player_infos.keySet()) {
                    if(!isDatabaseInfos(playerName)) {
                        Main.getInstance().getMysql().update("INSERT INTO " + PlayerInfos.TABLE + " (player_name, player_uuid) VALUES ('" + playerName + "', '" + player_infos.get(playerName) + "')");
                    }
                }

                //MUTES
                console.sendMessage(Main.getInstance().prefix + " §8- Saving muted...");
                for(UUID uuid : muted.keySet()) {
                    if(isDatabaseMuted(uuid)) {
                        Main.getInstance().getMysql().update("UPDATE " + MuteManager.TABLE + " SET end='" + muted.get(uuid) + "' WHERE player_uuid='" + uuid.toString() + "'");
                    } else {
                        Main.getInstance().getMysql().update("INSERT INTO " + MuteManager.TABLE + " (player_uuid, end) VALUES ('" + uuid.toString() + "', '" + muted.get(uuid) + "')");
                    }
                }

                //UNMUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unmuted...");
                for(UUID uuid : unmuted) {
                    Main.getInstance().getMysql().update("DELETE FROM " + MuteManager.TABLE + " WHERE player_uuid='" + uuid.toString() + "'");
                }
                console.sendMessage(Main.getInstance().prefix + "§aCache saved!");
            }

        //HARD DISK
        } else {
            console.sendMessage(Main.getInstance().prefix + "§aUsing hard disk");

            //START
            if(banned == null) {
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

                //PLAYER_INFOS
                player_infos = Main.getInstance().infosYML.read();

                //MUTES
                muted = Main.getInstance().muteYML.read();

                console.sendMessage(Main.getInstance().prefix + "§aCached!");

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

                //PLAYER_INFOS
                console.sendMessage(Main.getInstance().prefix + " §8- Updating player infos...");
                for(String playerName : player_infos.keySet()) {
                    if(!Main.getInstance().infosYML.exist(playerName)) {
                        Main.getInstance().infosYML.write(playerName, player_infos.get(playerName));
                    }
                }

                //MUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Saving muted...");
                for(UUID uuid : muted.keySet()) {
                    Main.getInstance().muteYML.write(uuid, muted.get(uuid));
                }

                //UNMUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unmuted...");
                for(UUID uuid : unmuted) {
                    Main.getInstance().muteYML.delete(uuid);
                }
                console.sendMessage(Main.getInstance().prefix + "§aCache saved!");
            }
        }

    }

    //PLAYER_INFOS
    public void addInfo(String playerName, UUID uuid) {
        if(!player_infos.containsValue(uuid))
            player_infos.put(playerName, uuid);
    }

    public boolean existInfo(String playerName) {
        for(String name : player_infos.keySet()) {
            if(name.equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    public UUID getUuid(String playerName) {
        for(String name : player_infos.keySet()) {
            if(name.equalsIgnoreCase(playerName)) {
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

    //MUTES
    public void mute(UUID uuid, long end) {
        if(!muted.containsKey(uuid)) {
            muted.put(uuid, Long.valueOf(end));
        }

        if(unmuted.contains(uuid)) unmuted.remove(uuid);
    }

    public void unmute(UUID uuid) {
        muted.remove(uuid);
        unmuted.add(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return muted.containsKey(uuid);
    }

    public boolean isDatabaseMuted(UUID uuid) {
        return (boolean) Main.getInstance().getMysql().query("SELECT * FROM " + MuteManager.TABLE + " WHERE player_uuid='" + uuid.toString() + "'", rs -> {
            try {
                return rs.next();
            } catch(SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public long getMuteEnd(UUID uuid) {
        return muted.get(uuid);
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
