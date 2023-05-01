package vg.h2o.deathcoordinate

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import vg.h2o.deathcoordinate.commands.RemoveItemDisplayTimerCommand
import vg.h2o.deathcoordinate.handlers.ItemDespawnTimerHandler
import vg.h2o.deathcoordinate.listeners.PlayerListener

class DeathCoordinate : JavaPlugin() {

    override fun onEnable() {
        logger.info("DeathCoordinate is running")

        Bukkit.getPluginManager().registerEvents(PlayerListener, this)

        getCommand("removeItemDespawnTimer")!!.setExecutor(RemoveItemDisplayTimerCommand)

        server.scheduler.runTaskTimer(this, Runnable {
            ItemDespawnTimerHandler.tick()
        }, 0, 1)
    }

    override fun onDisable() {
        logger.info("DeathCoordinate is stopped")
    }
}