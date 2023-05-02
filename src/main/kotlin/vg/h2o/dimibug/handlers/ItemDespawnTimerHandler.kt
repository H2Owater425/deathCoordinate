package vg.h2o.dimibug.handlers

import org.bukkit.entity.Player
import vg.h2o.dimibug.ItemDespawnTimer
import java.util.*

object ItemDespawnTimerHandler {

    private val timers = mutableMapOf<UUID, ItemDespawnTimer>()
    private var tick = 0

    fun init(player: Player) = timers.getOrPut(player.uniqueId) { ItemDespawnTimer() }.apply {
        changePlayer(player)
    }

    fun getTimer(player: Player) = timers[player.uniqueId] ?: init(player)

    fun tick() {
        tick = (tick + 1) % 20
        timers.values.forEach { it.tick(tick) }
    }
}