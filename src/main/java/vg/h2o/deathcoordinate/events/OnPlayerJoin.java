package vg.h2o.deathcoordinate.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if(OnPlayerDeath.isPlayerItemDespawnTimerExists(playerUniqueId)) {
            OnPlayerDeath.setItemDespawnTimerPlayer(playerUniqueId, player);
            OnPlayerDeath.addItemDespawnTimerPlayer(playerUniqueId, player);
        }
    }
}
