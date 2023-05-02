package vg.h2o.dimibug.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import vg.h2o.dimibug.handlers.ItemDespawnTimerHandler

object PlayerListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        ItemDespawnTimerHandler.init(e.player)
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        ItemDespawnTimerHandler.getTimer(e.player).onDeath()
    }
}