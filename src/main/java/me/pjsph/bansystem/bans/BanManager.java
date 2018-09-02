package me.pjsph.bansystem.bans;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.utils.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanManager {

    public static final String TABLE = "bans";

    public void ban(UUID uuid, long endInSeconds, String reason) {
        if(Main.getInstance().cache.isBanned(uuid)) return;

        long endToMillis = endInSeconds * 1000;
        long end = endToMillis + System.currentTimeMillis();

        if(endInSeconds == -1) end = -1;

        Main.getInstance().cache.add(uuid, end, reason);

        if(Bukkit.getPlayer(uuid) != null) {
            Player target = Bukkit.getPlayer(uuid);
            target.kickPlayer("§cYou are banned from this server !\n " +
                                    "\n " +
                                    "§6Reason : §f" + reason + "\n " +
                                    "\n " +
                                    "§aTime left : §f" + getTimeLeft(uuid));
        }
    }

    public void unban(UUID uuid) {
        if(!Main.getInstance().cache.isBanned(uuid)) return;

        Main.getInstance().cache.remove(uuid);
    }

    public boolean isBanned(UUID uuid) {
        return Main.getInstance().cache.isBanned(uuid);
    }

    public void checkDuration(UUID uuid) {
        if(!Main.getInstance().cache.isBanned(uuid)) return;

        if(getEnd(uuid) == -1) return;

        if(getEnd(uuid) < System.currentTimeMillis()) {
            unban(uuid);
        }
    }

    public long getEnd(UUID uuid) {
        if(!Main.getInstance().cache.isBanned(uuid)) return 0;

        return Main.getInstance().cache.getEnd(uuid);
    }

    public String getTimeLeft(UUID uuid) {
        if(!Main.getInstance().cache.isBanned(uuid)) return "§cNot banned";

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

        return months + " " + TimeUnit.MOIS.getName() + ", " + days + " " + TimeUnit.JOUR.getName() + ", " + hours + " " + TimeUnit.HEURE.getName() + ", " + minutes + " " + TimeUnit.MINUTE.getName() + ", " + seconds + " " + TimeUnit.SECONDE.getName();
    }

    public String getReason(UUID uuid) {
        if(!Main.getInstance().cache.isBanned(uuid)) return "§cNot banned";

        return Main.getInstance().cache.getReason(uuid);
    }

}
