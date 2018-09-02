package me.pjsph.bansystem.mutes;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.utils.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteManager {

    public static final String TABLE = "mutes";

    public void mute(UUID uuid, long endInSeconds) {
        if(Main.getInstance().cache.isMuted(uuid)) return;

        long endToMillis = endInSeconds * 1000;
        long end = endToMillis + System.currentTimeMillis();

        if(endInSeconds == -1) end = -1;

        Main.getInstance().cache.mute(uuid, end);

        if(Bukkit.getPlayer(uuid) != null) {
            Player target = Bukkit.getPlayer(uuid);
            if(end == -1) {
                target.sendMessage("§cYou have been permanently muted");
            } else {
                target.sendMessage("§cYou have been muted for §e" + getTimeLeft(uuid));
            }
        }
    }

    public void unmute(UUID uuid) {
        if(!Main.getInstance().cache.isMuted(uuid)) return;

        Main.getInstance().cache.unmute(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return Main.getInstance().cache.isMuted(uuid);
    }

    public void checkDuration(UUID uuid) {
        if(!Main.getInstance().cache.isMuted(uuid)) return;

        if(getEnd(uuid) == -1) return;

        if(getEnd(uuid) < System.currentTimeMillis()) {
            unmute(uuid);
        }
    }

    public long getEnd(UUID uuid) {
        if(!Main.getInstance().cache.isMuted(uuid)) return 0;

        return Main.getInstance().cache.getMuteEnd(uuid);
    }

    public String getTimeLeft(UUID uuid) {
        if(!Main.getInstance().cache.isMuted(uuid)) return "§cNot muted";

        if(getEnd(uuid) == -1) return "§cPermanent";

        long timeLeft = (getEnd(uuid) - System.currentTimeMillis()) / 1000;
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        while(timeLeft >= TimeUnit.MOIS.getToSecond()) {
            months++;
            timeLeft -= TimeUnit.MOIS.getToSecond();
        }

        while(timeLeft >= TimeUnit.JOUR.getToSecond()) {
            days++;
            timeLeft -= TimeUnit.JOUR.getToSecond();
        }

        while(timeLeft >= TimeUnit.HEURE.getToSecond()) {
            hours++;
            timeLeft -= TimeUnit.HEURE.getToSecond();
        }

        while(timeLeft >= TimeUnit.MINUTE.getToSecond()) {
            minutes++;
            timeLeft -= TimeUnit.MINUTE.getToSecond();
        }

        while(timeLeft >= TimeUnit.SECONDE.getToSecond()) {
            seconds++;
            timeLeft -= TimeUnit.SECONDE.getToSecond();
        }

        return ((months > 0) ? (months + " " + TimeUnit.MOIS.getName() + ", ") : "") + ((days > 0) ? (days + " " + TimeUnit.JOUR.getName() + ", ") : "") + ((hours > 0) ? (hours + " " + TimeUnit.HEURE.getName() + ", ") : "") + ((minutes > 0) ? (minutes + " " + TimeUnit.MINUTE.getName() + ", ") : "") + ((seconds > 0) ? (seconds + " " + TimeUnit.SECONDE.getName()) : "");
    }

}
