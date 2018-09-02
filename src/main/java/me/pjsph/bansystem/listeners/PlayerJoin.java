package me.pjsph.bansystem.listeners;

import me.pjsph.bansystem.Main;
import me.pjsph.bansystem.infos.PlayerInfos;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerInfos playerInfos = new PlayerInfos();

        playerInfos.update(player);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        Main.getInstance().banManager.checkDuration(player.getUniqueId());

        if(Main.getInstance().banManager.isBanned(player.getUniqueId())) {

            e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            e.setKickMessage("§cYou are banned from this server!\n " +
                    "\n " +
                    "§6Reason : §f" + Main.getInstance().banManager.getReason(player.getUniqueId()) + "\n " +
                    "\n " +
                    "§aTime left : §f" + Main.getInstance().banManager.getTimeLeft(player.getUniqueId()));
        }
    }

}
