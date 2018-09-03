package me.pjsph.bansystem.cache;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.mutes.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class MuteCache {

    private Map<UUID, Long> muted;
    private List<UUID> unmuted = new ArrayList<>();

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void update() {
        //DATABASE
        if (Main.getInstance().configManager.USE_DATABASE) {

            //START
            if (muted == null) {
                muted = new HashMap<>();

                //MUTES
                Main.getInstance().getMysql().query("SELECT * FROM " + MuteManager.TABLE, rs -> {
                    try {
                        ResultSetMetaData metaData = rs.getMetaData();

                        while (rs.next()) {
                            String uuidName = rs.getString("player_uuid");
                            UUID uuid = UUID.fromString(uuidName);
                            long end = rs.getLong("end");

                            muted.put(uuid, Long.valueOf(end));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            //STOP
            } else {
                //MUTES
                console.sendMessage(Main.getInstance().prefix + " §8- Saving muted...");
                for (UUID uuid : muted.keySet()) {
                    if (isDatabaseMuted(uuid)) {
                        Main.getInstance().getMysql().update("UPDATE " + MuteManager.TABLE + " SET end='" + muted.get(uuid) + "' WHERE player_uuid='" + uuid.toString() + "'");
                    } else {
                        Main.getInstance().getMysql().update("INSERT INTO " + MuteManager.TABLE + " (player_uuid, end) VALUES ('" + uuid.toString() + "', '" + muted.get(uuid) + "')");
                    }
                }

                //UNMUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unmuted...");
                for (UUID uuid : unmuted) {
                    Main.getInstance().getMysql().update("DELETE FROM " + MuteManager.TABLE + " WHERE player_uuid='" + uuid.toString() + "'");
                }
            }

        //HARD DISK
        } else {

        }
            //START
            if (muted == null) {

                //MUTES
                muted = Main.getInstance().muteYML.read();

            //STOP
            } else {

                //MUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Saving muted...");
                for (UUID uuid : muted.keySet()) {
                    Main.getInstance().muteYML.write(uuid, muted.get(uuid));
                }

                //UNMUTED
                console.sendMessage(Main.getInstance().prefix + " §8- Deleting unmuted...");
                for (UUID uuid : unmuted) {
                    Main.getInstance().muteYML.delete(uuid);
                }
            }
    }

    //MUTES
    public void mute(UUID uuid, long end) {
        if (!muted.containsKey(uuid)) {
            muted.put(uuid, Long.valueOf(end));
        }

        if (unmuted.contains(uuid)) unmuted.remove(uuid);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public long getMuteEnd(UUID uuid) {
        return muted.get(uuid);
    }

    public Map<UUID, Long> getMuted() {
        return muted;
    }

    public List<UUID> getUnmuted() {
        return unmuted;
    }
}
