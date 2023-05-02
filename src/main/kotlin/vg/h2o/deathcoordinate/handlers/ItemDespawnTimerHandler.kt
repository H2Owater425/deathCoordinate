package vg.h2o.deathcoordinate.handlers

import org.bukkit.entity.Player
import vg.h2o.deathcoordinate.ItemDespawnTimer
import java.util.*

object ItemDespawnTimerHandler {

    private val timers = mutableMapOf<UUID, ItemDespawnTimer>()
    private var tick = 0

    fun init(player: Player) {
        timers.getOrPut(player.uniqueId) { ItemDespawnTimer() }.changePlayer(player)
    }

    fun getTimer(player: Player) = timers[player.uniqueId]!!

    fun tick() {
        tick = (tick + 1) % 20
        timers.values.forEach { it.tick(tick) }
    }
}