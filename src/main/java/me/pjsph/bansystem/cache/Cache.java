package me.pjsph.bansystem.cache;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;

public class Cache {

    private BanCache banCache;
    private MuteCache muteCache;
    private InfosCache infosCache;

    public Cache() {
        banCache = new BanCache();
        muteCache = new MuteCache();
        infosCache = new InfosCache();
    }

    public void update() {

        banCache.update();
        muteCache.update();
        infosCache.update();

    }

    //BANS
    public void ban(UUID uuid, long end, String reason) {
        banCache.ban(uuid, end, reason);
    }

    public void unban(UUID uuid) {
        banCache.unban(uuid);
    }

    public boolean isBanned(UUID uuid) {
        return banCache.isBanned(uuid);
    }

    public long getBanEnd(UUID uuid) {
        return banCache.getBanEnd(uuid);
    }

    public String getBanReason(UUID uuid) {
        return banCache.getBanReason(uuid);
    }

    //MUTES
    public void mute(UUID uuid, long end) {
        muteCache.mute(uuid, end);
    }

    public void unmute(UUID uuid) {
        muteCache.unmute(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return muteCache.isMuted(uuid);
    }

    public long getMuteEnd(UUID uuid) {
        return muteCache.getMuteEnd(uuid);
    }

    //PLAYER_INFOS
    public void addInfo(String playerName, UUID uuid) {
        infosCache.addInfo(playerName, uuid);
    }

    public boolean existInfo(String playerName) {
        return infosCache.existInfo(playerName);
    }

    public UUID getUuid(String playerName) {
        return infosCache.getUuid(playerName);
    }
}
