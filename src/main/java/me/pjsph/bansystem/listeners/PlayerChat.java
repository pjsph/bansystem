package me.pjsph.bansystem.listeners;

import me.pjsph.bansystem.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PlayerChat implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Main.getInstance().muteManager.checkDuration(uuid);

        if(Main.getInstance().muteManager.isMuted(uuid)) {
            e.setCancelled(true);
            if(Main.getInstance().muteManager.getTimeLeft(uuid).equalsIgnoreCase("§cPermanent")) {
                player.sendMessage("§cYou are permanently muted");
            } else {
                player.sendMessage("§cYou are muted for §e" + Main.getInstance().muteManager.getTimeLeft(uuid));
            }
        }
    }

}
