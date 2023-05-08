package vg.h2o.dimibug.handlers

import io.github.monun.kommand.wrapper.Position2D
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import vg.h2o.dimibug.DimiBug
import vg.h2o.dimibug.Utils
import vg.h2o.dimibug.arrow

object CompassHandler {

    private val compasses = mutableMapOf<Player, BukkitTask>()

    fun delete(player: Player) {
        println("$player deleted")
        compasses.remove(player)?.cancel()
    }

    fun create(player: Player, dest: Player) {
        delete(player)

        create(player) {
            dest.location
        }
    }

    fun create(player: Player, dest: Position2D) {
        create(player) {
            Location(player.world, dest.x, player.location.y, dest.z)
        }
    }

    private fun create(player: Player, provider: () -> Location) {
        delete(player)

        compasses[player] = Bukkit.getScheduler().runTaskTimer(DimiBug.instance, Runnable {

            if (!player.isOnline) {
                delete(player)
                return@Runnable
            }

            val dest = provider()

            if (player.world != dest.world) {
                return@Runnable
            }

            val direction = Utils.getDirectionArrow(player, dest)

            if (direction.distance <= 1) {
                delete(player)
                return@Runnable
            }

            val text = text("${"%.1f".format(direction.distance)}m ")
                .append(text(direction.arrow, if (direction.index == 0) NamedTextColor.GREEN else NamedTextColor.RED))

            player.sendActionBar(text)
        }, 0, 1)
    }
}