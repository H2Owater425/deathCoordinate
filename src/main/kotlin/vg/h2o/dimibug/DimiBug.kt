package vg.h2o.dimibug

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import vg.h2o.dimibug.commands.RemoveItemDisplayTimerCommand
import vg.h2o.dimibug.handlers.ItemDespawnTimerHandler
import vg.h2o.dimibug.listeners.PlayerListener

class DimiBug : JavaPlugin() {

    override fun onEnable() {
        logger.info("DimiBug is running")

        Bukkit.getPluginManager().registerEvents(PlayerListener, this)

        getCommand("removeItemDespawnTimer")!!.setExecutor(RemoveItemDisplayTimerCommand)

        server.scheduler.runTaskTimer(this, Runnable {
            ItemDespawnTimerHandler.tick()
        }, 0, 1)
    }

    override fun onDisable() {
        logger.info("DimiBug is stopped")
    }
}