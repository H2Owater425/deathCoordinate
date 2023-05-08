package vg.h2o.dimibug

import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.roundToInt

object Utils {

    fun getDirectionArrow(player: Player, dest: Location): DimiDirection {
        val direction = dest.clone().subtract(player.location).toVector()
        var angle = player.eyeLocation.clone().setDirection(direction).yaw

        if (angle < 0) {
            angle += 360
        }

        angle -= player.location.yaw

        if (angle < 0) {
            angle += 360
        }

        var index = ((angle / 45) % 8).roundToInt()
        if (index == 8) index = 0

        return DimiDirection(index, player.location.distance(dest))
    }
}