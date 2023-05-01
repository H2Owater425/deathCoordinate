package vg.h2o.deathcoordinate.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerRespawn implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        OnPlayerDeath.setItemDespawnTimerIsPlayerRespawned(playerRespawnEvent.getPlayer().getUniqueId());

        return;
    }
}
