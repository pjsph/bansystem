package me.pjsph.bansystem.listeners;

import me.pjsph.bansystem.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PlayerChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Main.getInstance().muteManager.checkDuration(uuid);

        if(Main.getInstance().muteManager.isMuted(uuid)) {
            e.setCancelled(true);
            player.sendMessage("§cYou have been muted for §e" + Main.getInstance().muteManager.getTimeLeft(uuid));
        }
    }

}
